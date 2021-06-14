package com.example.codeexp2021app;

import android.media.AudioRecord;
import android.text.TextUtils;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.codeexp2021app.api.ApiClient;
import com.example.codeexp2021app.api.model.sogou.CreateTokenResult;
import com.example.codeexp2021app.base.BaseViewModel;
import com.example.codeexp2021app.config.Config;
import com.example.codeexp2021app.constants.Constants;
import com.example.codeexp2021app.network.http.ResponseCode;
import com.example.codeexp2021app.network.http.Result;
import com.example.codeexp2021app.thread.ThreadManager;
import com.example.codeexp2021app.utils.ContextUtils;
import com.example.codeexp2021app.utils.FileUtils;
import com.github.piasy.rxandroidaudio.StreamAudioPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends BaseViewModel {

    private static final int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(Constants.SAMPLE_RATE_HERTZ, Constants.CHANNEL_CONFIG, Constants.AUDIO_FORMAT);

    public MutableLiveData<Boolean> mEnableStartCapturing = new MutableLiveData<>();
    public MutableLiveData<Boolean> mEnableStopCapturing = new MutableLiveData<>();
    public MutableLiveData<Boolean> mEnablePlay = new MutableLiveData<>();
    public MutableLiveData<String> mErrorMsg = new MutableLiveData<>();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {

    }

    public void initCapturingBtnState() {
        mEnableStartCapturing.postValue(false);
        mEnableStopCapturing.postValue(false);
        mEnablePlay.postValue(false);
    }

    public void startCapturingBtnState() {
        mEnableStartCapturing.postValue(false);
        mEnableStopCapturing.postValue(true);
        mEnablePlay.postValue(false);
    }

    public void stopCapturingBtnState() {
        mEnableStartCapturing.postValue(true);
        mEnableStopCapturing.postValue(false);
        mEnablePlay.postValue(true);
    }

    public void startPlayingBtnState() {
        mEnablePlay.postValue(false);
    }

    public void stopPlayingBtnState() {
        mEnablePlay.postValue(true);
    }

    public void createToken() {
        ThreadManager.getThreadPollProxy().execute(new Runnable() {
            @Override
            public void run() {
                Result<CreateTokenResult> result = ApiClient.createToken(Constants.APP_ID, Constants.APP_KEY, Duration.ofHours(1));
                if (result.isSuccess()) {
                    CreateTokenResult body = result.getBody(CreateTokenResult.class);
                    Config.setSogouToken(body.getToken());
                    stopCapturingBtnState();
                } else {
                    doResultErrorMsg(result);
                }
            }
        });
    }

    /**
     * 处理网络请求结果的错误信息
     *
     * @param result
     */
    private void doResultErrorMsg(Result result) {
        if (result.getCode() == ResponseCode.NETWORK_ERROR) {
            mErrorMsg.postValue(ContextUtils.getContext().getString(R.string.network_or_server_error_str));
        } else if (result.getCode() == ResponseCode.NOT_FOUND) {
            mErrorMsg.postValue(ContextUtils.getContext().getString(R.string.website_format_error));
        } else {
            mErrorMsg.postValue(ContextUtils.getContext().getString(R.string.network_or_server_error_str));
        }
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

    public void play() {
        File outputFile = getAudioCaptureFile();
        if (outputFile == null) {
            return;
        }
        Observable.just(outputFile).subscribeOn(Schedulers.io()).subscribe(new Consumer<File>() {
            @Override
            public void accept(File file) {
                try {
                    StreamAudioPlayer mStreamAudioPlayer = StreamAudioPlayer.getInstance();
                    mStreamAudioPlayer.init();
                    FileInputStream inputStream = new FileInputStream(file);
                    int read;
                    byte[] mBuffer = new byte[1024];
                    while ((read = inputStream.read(mBuffer)) > 0) {
                        mStreamAudioPlayer.play(mBuffer, read);
                    }
                    inputStream.close();
                    mStreamAudioPlayer.release();
                    stopPlayingBtnState();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
