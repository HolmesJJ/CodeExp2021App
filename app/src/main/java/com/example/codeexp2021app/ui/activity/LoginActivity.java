package com.example.codeexp2021app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.codeexp2021app.BR;
import com.example.codeexp2021app.R;
import com.example.codeexp2021app.base.BaseActivity;
import com.example.codeexp2021app.databinding.ActivityLoginBinding;
import com.example.codeexp2021app.listener.OnMultiClickListener;
import com.example.codeexp2021app.ui.viewmodel.LoginViewModel;
import com.example.codeexp2021app.utils.ListenerUtils;
import com.example.codeexp2021app.utils.ToastUtils;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_login;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public Class<LoginViewModel> getViewModelClazz() {
        return LoginViewModel.class;
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
        mViewModel.mActivityAction.observe(this, activityAction -> {
            stopLoading();
            if (activityAction != null) {
                try {
                    startActivity(activityAction);
                    finish();
                } catch (Exception e) {
                    ToastUtils.showShortSafe(e.getMessage());
                }
            } else {
                Log.e(TAG, "activityAction is null");
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
                    mViewModel.updateLoginBtnState();
                }
            }
        });
        ListenerUtils.addTextChangeListener(mBinding.etPassword, new ListenerUtils.TextChange() {
            @Override
            public void textChange(String s) {
                if (mViewModel != null) {
                    String value = s == null ? "" : s;
                    mViewModel.mPassword.set(value);
                    mViewModel.updateLoginBtnState();
                }
            }
        });
    }

    private void setClickListener() {
        ListenerUtils.setOnClickListener(mBinding.btnLogin, new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (mViewModel != null && mViewModel.mEnableLogin.get()) {
                    showLoading(false);
                    mViewModel.login();
                }
            }
        });
    }
}