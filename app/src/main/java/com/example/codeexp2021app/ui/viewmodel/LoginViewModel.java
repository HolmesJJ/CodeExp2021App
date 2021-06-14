package com.example.codeexp2021app.ui.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.codeexp2021app.MainActivity;
import com.example.codeexp2021app.R;
import com.example.codeexp2021app.api.ApiClient;
import com.example.codeexp2021app.api.model.login.LoginResult;
import com.example.codeexp2021app.base.BaseViewModel;
import com.example.codeexp2021app.network.http.ResponseCode;
import com.example.codeexp2021app.network.http.Result;
import com.example.codeexp2021app.thread.ThreadManager;
import com.example.codeexp2021app.utils.ContextUtils;

public class LoginViewModel extends BaseViewModel {

    public ObservableField<String> mUsername = new ObservableField<>();
    public ObservableField<String> mPassword = new ObservableField<>();
    public ObservableBoolean mEnableLogin = new ObservableBoolean();
    public MutableLiveData<String> mErrorMsg = new MutableLiveData<>();
    public MutableLiveData<Class> mActivityAction = new MutableLiveData<>();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {

    }

    public void updateLoginBtnState() {
        // 当账号或者密码为空的时候不允许登录
        String account = mUsername.get();
        String password = mPassword.get();
        mEnableLogin.set(account != null && !"".equals(account) && password != null && !"".equals(password));
    }

    public void login() {
//        String username = mUsername.get();
//        String password = mPassword.get();
//        ThreadManager.getThreadPollProxy().execute(new Runnable() {
//            @Override
//            public void run() {
//                Result<LoginResult> result = ApiClient.login(username, password);
//                if (result.isSuccess()) {
//                    LoginResult body = result.getBody(LoginResult.class);
//                    mActivityAction.postValue(MainActivity.class);
//                } else {
//                    doResultErrorMsg(result);
//                }
//            }
//        });

        mActivityAction.postValue(MainActivity.class);
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
}
