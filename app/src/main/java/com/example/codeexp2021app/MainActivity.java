package com.example.codeexp2021app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Observer;

import com.example.codeexp2021app.base.BaseActivity;
import com.example.codeexp2021app.constants.Constants;
import com.example.codeexp2021app.databinding.ActivityMainBinding;
import com.example.codeexp2021app.listener.OnMultiClickListener;
import com.example.codeexp2021app.service.AudioCaptureService;
import com.example.codeexp2021app.utils.ContextUtils;
import com.example.codeexp2021app.utils.ListenerUtils;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public Class<MainViewModel> getViewModelClazz() {
        return MainViewModel.class;
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        setObserveListener();
        setClickListener();
        mViewModel.initCapturingBtnState();
        if (mViewModel != null) {
            mViewModel.createToken();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setObserveListener() {
        mViewModel.mEnableStartCapturing.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mBinding.btnStartCapturing.setEnabled(aBoolean);
            }
        });
        mViewModel.mEnableStopCapturing.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mBinding.btnStopCapturing.setEnabled(aBoolean);
            }
        });
        mViewModel.mEnablePlay.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mBinding.btnPlay.setEnabled(aBoolean);
            }
        });
        mViewModel.mErrorMsg.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                if (mBinding != null) {
                    mBinding.tvErrorMsg.setText(value);
                }
            }
        });
    }

    private void setClickListener() {
        ListenerUtils.setOnClickListener(mBinding.btnStartCapturing, new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                startAudioCaptureService();
                mViewModel.startCapturingBtnState();
            }
        });
        ListenerUtils.setOnClickListener(mBinding.btnStopCapturing, new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                stopAudioCaptureService();
                mViewModel.stopCapturingBtnState();
            }
        });
        ListenerUtils.setOnClickListener(mBinding.btnPlay, new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                mViewModel.startPlayingBtnState();
                mViewModel.play();
            }
        });
    }

    public void startAudioCaptureService() {
        Intent serviceIntent = new Intent(ContextUtils.getContext(), AudioCaptureService.class);
        serviceIntent.setAction(Constants.AUDIO_CAPTURE_SERVICE_START);
        startForegroundService(serviceIntent);
    }

    public void stopAudioCaptureService() {
        Intent serviceIntent = new Intent(ContextUtils.getContext(), AudioCaptureService.class);
        serviceIntent.setAction(Constants.AUDIO_CAPTURE_SERVICE_STOP);
        stopService(serviceIntent);
    }
}