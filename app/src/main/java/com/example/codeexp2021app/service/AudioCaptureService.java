package com.example.codeexp2021app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSON;
import com.example.codeexp2021app.R;
import com.example.codeexp2021app.api.model.sogou.AudioConfigParameter;
import com.example.codeexp2021app.api.model.sogou.StreamConfigParameter;
import com.example.codeexp2021app.config.Config;
import com.example.codeexp2021app.constants.Constants;
import com.example.codeexp2021app.network.websocket.WebSocketClientManager;
import com.example.codeexp2021app.thread.CustomThreadPool;
import com.example.codeexp2021app.utils.ContextUtils;
import com.example.codeexp2021app.utils.FileUtils;
import com.example.codeexp2021app.utils.ToastUtils;
import com.github.piasy.rxandroidaudio.StreamAudioRecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class AudioCaptureService extends Service implements StreamAudioRecorder.AudioDataCallback {

    private static final String TAG = AudioCaptureService.class.getSimpleName();

    private static final CustomThreadPool threadPoolCaptureAudio = new CustomThreadPool(Thread.MAX_PRIORITY);
    private static final CustomThreadPool threadPoolSendAudio = new CustomThreadPool(Thread.MAX_PRIORITY);

    private StreamAudioRecorder mStreamAudioRecorder;
    private FileOutputStream mFileOutputStream;
    private volatile boolean mIsRecording;
    private volatile boolean mIsSendAudioReady;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction().equals(Constants.AUDIO_CAPTURE_SERVICE_START)) {
            Notification notification = new NotificationCompat.Builder(ContextUtils.getContext(), Constants.AUDIO_CAPTURE_SERVICE_CHANNEL)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.audio_capturing))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            startForeground(Constants.AUDIO_CAPTURE_SERVICE_CHANNEL_ID, notification);
            startCaptureAudioTask();
            // 系统被杀死后将尝试重新创建服务
            startSendAudioTask();
            return START_STICKY;
        } else {
            stopCaptureAudioTask();
            stopForeground(true);
            stopSelf();
            // 系统被终止后将不会尝试重新创建服务
            return START_NOT_STICKY;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopCaptureAudioTask();
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                Constants.AUDIO_CAPTURE_SERVICE_CHANNEL,
                Constants.AUDIO_CAPTURE_SERVICE_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    private void startCaptureAudioTask() {
        File outputFile = createAudioFile();
        Log.d(TAG, "Created file for capture target: " + outputFile.getAbsolutePath());
        try {
            mFileOutputStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        threadPoolCaptureAudio.execute(() -> {
            mIsRecording = true;
            mStreamAudioRecorder = StreamAudioRecorder.getInstance();
            mStreamAudioRecorder.start(AudioCaptureService.this);
            startSendAudioTask();
        });
    }

    private File createAudioFile() {
        File audioCapturesDirectory = new File(FileUtils.APP_DIR, Constants.AUDIO_CAPTURE_DIRECTORY);
        FileUtils.deleteDirectory(audioCapturesDirectory.getAbsolutePath());
        if (!audioCapturesDirectory.exists()) {
            audioCapturesDirectory.mkdirs();
        }
        return new File(audioCapturesDirectory.getAbsolutePath() + File.separator + Constants.AUDIO_CAPTURE_FILE);
    }

    private void stopCaptureAudioTask() {
        stopSendAudioTask();
        if (mStreamAudioRecorder != null) {
            mStreamAudioRecorder.stop();
            mStreamAudioRecorder = null;
        }
        threadPoolCaptureAudio.release();
        mIsRecording = false;
    }

    private void beforeSendAudioTask() throws URISyntaxException {
        Map<String, String> authHeader = new HashMap<>();
        authHeader.put("Appid", Constants.APP_ID);
        authHeader.put("Authorization", "Bearer " + Config.sSogouToken);
        Log.i(TAG, "[Appid: " + authHeader.get("Appid") + ", Authorization " + authHeader.get("Authorization") + "]");
        WebSocketClientManager.getInstance().connect(new URI(Constants.SOGOU_WSS + "asr/v1/streaming_recognize"), authHeader);
        WebSocketClientManager.getInstance().connectBlocking();
        AudioConfigParameter audioConfigParameter = new AudioConfigParameter();
        audioConfigParameter.setEncoding("LINEAR16");
        audioConfigParameter.setSampleRateHertz(16000);
        audioConfigParameter.setLanguageCode("zh-cmn-Hans-CN");
        StreamConfigParameter streamConfigParameter = new StreamConfigParameter();
        streamConfigParameter.setAudioConfigParameter(audioConfigParameter);
        streamConfigParameter.setInterimResults(true);
        WebSocketClientManager.getInstance().sendMessage(JSON.toJSONString(streamConfigParameter));
    }

    private void startSendAudioTask() {
        threadPoolSendAudio.execute(() -> {
            try {
                beforeSendAudioTask();
                mIsSendAudioReady = true;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    private void stopSendAudioTask() {
        WebSocketClientManager.getInstance().sendMessage("{}");
        WebSocketClientManager.getInstance().disconnect();
        threadPoolSendAudio.release();
        mIsSendAudioReady = false;
    }

    @Override
    public void onAudioData(byte[] data, int size) {
        if (mFileOutputStream != null && mIsSendAudioReady) {
            try {
                mFileOutputStream.write(data, 0, size);
                WebSocketClientManager.getInstance().sendData(ByteBuffer.wrap(data, 0, size));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError() {
        stopCaptureAudioTask();
        ToastUtils.showShortSafe("Record fail");
    }

    private File getAudioCaptureFile() {
        File audioCapturesDirectory = new File(FileUtils.APP_DIR, Constants.AUDIO_CAPTURE_DIRECTORY);
        if (!audioCapturesDirectory.exists()) {
            return null;
        }
        File audioCaptureFile = new File(audioCapturesDirectory, Constants.AUDIO_CAPTURE_FILE);
        if (!audioCaptureFile.exists()) {
            return null;
        }
        return audioCaptureFile;
    }

    private void test() {
        File outputFile = getAudioCaptureFile();
        if (outputFile == null) {
            return;
        }
        try {
            FileInputStream inputStream = new FileInputStream(outputFile);
            sendAudio(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendAudio(final InputStream in) {
        threadPoolSendAudio.execute(() -> {
            try {
                byte[] buf = new byte[3200];
                for(;;) {
                    int n = in.read(buf);
                    if (n <= 0) {
                        break;
                    }
                    WebSocketClientManager.getInstance().sendData(ByteBuffer.wrap(buf, 0, n));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            WebSocketClientManager.getInstance().sendMessage("{}");
        });
    }
}