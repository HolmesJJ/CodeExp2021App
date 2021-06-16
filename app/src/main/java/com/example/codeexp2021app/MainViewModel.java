package com.example.codeexp2021app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.codeexp2021app.api.ApiClient;
import com.example.codeexp2021app.api.model.google.CreateTokenResult;
import com.example.codeexp2021app.base.BaseViewModel;
import com.example.codeexp2021app.bluetooth.BluetoothHelper;
import com.example.codeexp2021app.config.Config;
import com.example.codeexp2021app.constants.MessageType;
import com.example.codeexp2021app.listener.BluetoothListener;
import com.example.codeexp2021app.media.AudioRecordHelper;
import com.example.codeexp2021app.network.http.ResponseCode;
import com.example.codeexp2021app.network.http.Result;
import com.example.codeexp2021app.thread.ThreadManager;
import com.example.codeexp2021app.utils.ContextUtils;
import com.example.codeexp2021app.utils.ToastUtils;

public class MainViewModel extends BaseViewModel implements BluetoothListener {

    private static final String TAG = MainViewModel.class.getSimpleName();

    /** We reuse an access token if its expiration time is longer than this. */
    public static final int ACCESS_TOKEN_EXPIRATION_TOLERANCE = 30 * 60 * 1000; // thirty minutes
    /** We refresh the current access token before it expires. */
    public static final int ACCESS_TOKEN_FETCH_MARGIN = 60 * 1000; // one minute

    public MutableLiveData<Boolean> mEnableStartCapturing = new MutableLiveData<>();
    public MutableLiveData<Boolean> mEnableStopCapturing = new MutableLiveData<>();
    public MutableLiveData<String> mErrorMsg = new MutableLiveData<>();

    private static Handler mHandler;

    private final Runnable mFetchAccessTokenRunnable = new Runnable() {
        @Override
        public void run() {
            createToken();
        }
    };

    private final BroadcastReceiver mRecognizedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("text");
            boolean isFinal = intent.getBooleanExtra("isFinal", true);
            if (isFinal) {
                AudioRecordHelper.getInstance().dismiss();
            }
            if (!TextUtils.isEmpty(text)) {
                BluetoothHelper.getInstance().send(text);
            }
        }
    };

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate() {
        mHandler = new Handler();
        LocalBroadcastManager.getInstance(ContextUtils.getContext()).registerReceiver(mRecognizedBroadcastReceiver, new IntentFilter(MessageType.RECOGNIZED.getValue()));
        BluetoothHelper.getInstance().init(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {
        mHandler.removeCallbacks(mFetchAccessTokenRunnable);
        mHandler = null;
        BluetoothHelper.getInstance().release();
    }

    public void initCapturingBtnState() {
        mEnableStartCapturing.postValue(false);
        mEnableStopCapturing.postValue(false);
    }

    public void startCapturingBtnState() {
        mEnableStartCapturing.postValue(false);
        mEnableStopCapturing.postValue(true);
    }

    public void stopCapturingBtnState() {
        mEnableStartCapturing.postValue(true);
        mEnableStopCapturing.postValue(false);
    }

    public void createToken() {
        // Check if the current token is still valid for a while
        if (!TextUtils.isEmpty(Config.sToken) && Config.sExpirationTime > 0) {
            if (Config.sExpirationTime > System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TOLERANCE) {
                stopCapturingBtnState();
                scheduleRefreshToken();
                return;
            }
        }
        ThreadManager.getThreadPollProxy().execute(new Runnable() {
            @Override
            public void run() {
                Result<CreateTokenResult> result = ApiClient.createToken();
                if (result.isSuccess()) {
                    CreateTokenResult body = result.getBody(CreateTokenResult.class);
                    Config.setToken(body.getToken());
                    Config.setExpirationTime(body.getExpirationTime());
                    stopCapturingBtnState();
                    scheduleRefreshToken();
                } else {
                    doResultErrorMsg(result);
                }
            }
        });
    }

    /**
     * Schedule access token refresh before it expires
     */
    private void scheduleRefreshToken() {
        if (mHandler != null) {
            mHandler.postDelayed(mFetchAccessTokenRunnable,
                    Math.max(Config.sExpirationTime - System.currentTimeMillis() - ACCESS_TOKEN_FETCH_MARGIN, ACCESS_TOKEN_EXPIRATION_TOLERANCE));
        }
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

    @Override
    public void onListening() {
        Log.i(TAG, "onListening");
        ToastUtils.showShortSafe("Bluetooth Listening...");
    }

    @Override
    public void onConnecting() {
        Log.i(TAG, "onConnecting");
        ToastUtils.showShortSafe("Bluetooth Connecting...");
    }

    @Override
    public void onConnected() {
        Log.i(TAG, "onConnected");
        ToastUtils.showShortSafe("Bluetooth Connected...");
    }

    @Override
    public void onDisconnected() {
        Log.i(TAG, "onDisconnected");
        ToastUtils.showShortSafe("Bluetooth Disconnected...");
    }

    @Override
    public void onConnectionFailed() {
        Log.i(TAG, "onConnectionFailed");
        ToastUtils.showShortSafe("Bluetooth Connection Failed...");
    }

    @Override
    public void onMessageReceived(String message) {
        Log.i(TAG, "onMessageReceived " + message);
    }
}
