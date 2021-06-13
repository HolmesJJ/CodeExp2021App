package com.example.codeexp2021app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.codeexp2021app.base.BaseActivity;
import com.example.codeexp2021app.databinding.ActivityMainBinding;
import com.example.codeexp2021app.listener.OnMultiClickListener;
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
        mViewModel.mUsername.set(mBinding.etUsername.getText().toString());
        mViewModel.mPassword.set(mBinding.etPassword.getText().toString());
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        setObserveListener();
        setTextChangeListener();
        setClickListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setObserveListener() {
        mViewModel.mErrorMsg.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String value) {
                stopLoading();
                if (mBinding != null) {
                    mBinding.tvErrorMsg.setText(value);
                }
            }
        });
    }

    private void setTextChangeListener() {
        ListenerUtils.addTextChangeListener(mBinding.etUsername, new ListenerUtils.TextChange() {
            @Override
            public void textChange(String s) {
                if (mViewModel != null) {
                    String value = s == null ? "" : s;
                    mViewModel.mUsername.set(value);
                }
            }
        });
        ListenerUtils.addTextChangeListener(mBinding.etPassword, new ListenerUtils.TextChange() {
            @Override
            public void textChange(String s) {
                if (mViewModel != null) {
                    String value = s == null ? "" : s;
                    mViewModel.mPassword.set(value);
                }
            }
        });
    }

    private void setClickListener() {
        ListenerUtils.setOnClickListener(mBinding.btnLogin, new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (TextUtils.isEmpty(mViewModel.mUsername.get())) {
                    mBinding.tvErrorMsg.setText(R.string.empty_username);
                    return;
                }
                if (TextUtils.isEmpty(mViewModel.mPassword.get())) {
                    mBinding.tvErrorMsg.setText(R.string.empty_password);
                    return;
                }
                if (mViewModel != null) {
                    showLoading(false);
                    mViewModel.login();
                }
            }
        });
    }
}