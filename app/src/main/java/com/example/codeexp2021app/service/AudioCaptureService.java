package com.example.codeexp2021app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.codeexp2021app.R;
import com.example.codeexp2021app.api.interceptor.google.GoogleCredentialsInterceptor;
import com.example.codeexp2021app.config.Config;
import com.example.codeexp2021app.constants.Constants;
import com.example.codeexp2021app.constants.MessageType;
import com.example.codeexp2021app.listener.AudioRecordListener;
import com.example.codeexp2021app.media.AudioRecordHelper;
import com.example.codeexp2021app.thread.CustomThreadPool;
import com.example.codeexp2021app.utils.ContextUtils;
import com.example.codeexp2021app.utils.FileUtils;
import com.example.codeexp2021app.utils.LanguageUtils;
import com.example.codeexp2021app.utils.PcmToWavUtils;
import com.example.codeexp2021app.utils.ToastUtils;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechGrpc;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.okhttp.OkHttpChannelProvider;
import io.grpc.stub.StreamObserver;

public class AudioCaptureService extends Service implements AudioRecordListener {

    private static final String TAG = AudioCaptureService.class.getSimpleName();

    private static final CustomThreadPool threadPoolCaptureAudio = new CustomThreadPool(Thread.MAX_PRIORITY);
    private static final CustomThreadPool threadPoolRecognizing = new CustomThreadPool(Thread.MAX_PRIORITY);

    private SpeechGrpc.SpeechStub mApi;
    private StreamObserver<StreamingRecognizeRequest> mRequestObserver;

    private final StreamObserver<StreamingRecognizeResponse> mResponseObserver
            = new StreamObserver<StreamingRecognizeResponse>() {
        @Override
        public void onNext(StreamingRecognizeResponse response) {
            String text = null;
            boolean isFinal = false;
            if (response.getResultsCount() > 0) {
                final StreamingRecognitionResult result = response.getResults(0);
                isFinal = result.getIsFinal();
                if (result.getAlternativesCount() > 0) {
                    final SpeechRecognitionAlternative alternative = result.getAlternatives(0);
                    text = alternative.getTranscript();
                }
            }
            if (text != null) {
                Intent recognizedIntent = new Intent(MessageType.RECOGNIZED.getValue());
                recognizedIntent.putExtra("text", text);
                recognizedIntent.putExtra("isFinal", isFinal);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(recognizedIntent);
            }
        }

        @Override
        public void onError(Throwable t) {
            Log.e(TAG, "Error calling the API.", t);
            ToastUtils.showShortSafe("Error calling the API");
            finishRecognizing();
            startRecognizing(AudioRecordHelper.getInstance().getSampleRate());
        }

        @Override
        public void onCompleted() {
            Log.i(TAG, "API completed.");
        }
    };

    private FileOutputStream mFileOutputStream;
    private int sampleRate;

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
            initGRPC();
            AudioRecordHelper.getInstance().init(this);
            startCaptureAudioTask();
            // 系统被杀死后将尝试重新创建服务
            return START_STICKY;
        } else {
            stopCaptureAudioTask();
            AudioRecordHelper.getInstance().release();
            releaseGRPC();
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
        AudioRecordHelper.getInstance().release();
        releaseGRPC();
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

    private void initGRPC() {
        AccessToken accessToken = new AccessToken(Config.sToken, new Date(Config.sExpirationTime));
        ManagedChannel channel = new OkHttpChannelProvider()
                .builderForAddress(Constants.HOSTNAME, Constants.PORT)
                .nameResolverFactory(new DnsNameResolverProvider())
                .intercept(new GoogleCredentialsInterceptor(new GoogleCredentials(accessToken)
                        .createScoped(Constants.SCOPE)))
                .build();
        mApi = SpeechGrpc.newStub(channel);
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
            AudioRecordHelper.getInstance().start();
            AudioRecordHelper.getInstance().process();
        });
    }

    private void stopCaptureAudioTask() {
        AudioRecordHelper.getInstance().stop();
        mFileOutputStream = null;
        convertPcmToWav();
        ToastUtils.showShortSafe("Pcm to Wav converted");
    }

    /**
     * Starts recognizing speech audio.
     *
     * @param sampleRate The sample rate of the audio.
     */
    private void startRecognizing(int sampleRate) {
        if (mApi == null) {
            Log.w(TAG, "API not ready. Ignoring the request.");
            return;
        }
        threadPoolRecognizing.execute(() -> {
            // Configure the API
            mRequestObserver = mApi.streamingRecognize(mResponseObserver);
            mRequestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(StreamingRecognitionConfig.newBuilder()
                            .setConfig(RecognitionConfig.newBuilder()
                                    .setLanguageCode(LanguageUtils.getDefaultLanguageCode())
                                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                                    .setSampleRateHertz(sampleRate)
                                    .build())
                            .setInterimResults(true)
                            .setSingleUtterance(true)
                            .build())
                    .build());
        });
    }

    /**
     * Recognizes the speech audio. This method should be called every time a chunk of byte buffer
     * is ready.
     *
     * @param data The audio data.
     * @param size The number of elements that are actually relevant in the {@code data}.
     */
    private void recognize(byte[] data, int size) {
        if (mRequestObserver == null) {
            return;
        }
        // Call the streaming recognition API
        mRequestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                .setAudioContent(ByteString.copyFrom(data, 0, size))
                .build());
    }

    /**
     * Finishes recognizing speech audio.
     */
    private void finishRecognizing() {
        if (mRequestObserver == null) {
            return;
        }
        mRequestObserver.onCompleted();
        mRequestObserver = null;
    }

    private void releaseGRPC() {
        if (mApi != null) {
            final ManagedChannel channel = (ManagedChannel) mApi.getChannel();
            if (channel != null && !channel.isShutdown()) {
                try {
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error shutting down the gRPC channel.", e);
                }
            }
            mApi = null;
        }
    }

    private File createAudioFile() {
        File audioCapturesDirectory = new File(FileUtils.APP_DIR, Constants.AUDIO_CAPTURE_DIRECTORY);
        FileUtils.deleteDirectory(audioCapturesDirectory.getAbsolutePath());
        if (!audioCapturesDirectory.exists()) {
            audioCapturesDirectory.mkdirs();
        }
        return new File(audioCapturesDirectory.getAbsolutePath() + File.separator + Constants.AUDIO_CAPTURE_PCM);
    }

    private void convertPcmToWav() {
        File audioCapturesDirectory = new File(FileUtils.APP_DIR, Constants.AUDIO_CAPTURE_DIRECTORY);
        if (!audioCapturesDirectory.exists()) {
            return;
        }
        PcmToWavUtils pcmToWavUtils = new PcmToWavUtils(sampleRate, Constants.CHANNEL_IN, Constants.ENCODING);
        File audioCapturePCM = new File(audioCapturesDirectory, Constants.AUDIO_CAPTURE_PCM);
        File audioCaptureWAV = new File(audioCapturesDirectory, Constants.AUDIO_CAPTURE_WAV);
        if (audioCaptureWAV.exists()) {
            audioCaptureWAV.delete();
        }
        pcmToWavUtils.pcmToWav(audioCapturePCM.getAbsolutePath(), audioCaptureWAV.getAbsolutePath());
    }

    @Override
    public void onAudioStart() {
        sampleRate = AudioRecordHelper.getInstance().getSampleRate();
        startRecognizing(sampleRate);
    }

    @Override
    public void onAudio(byte[] data, int size) {
        recognize(data, size);
    }

    @Override
    public void onAudioEnd() {
        finishRecognizing();
    }

    @Override
    public void onFile(byte[] data, int size) {
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.write(data, 0, size);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}