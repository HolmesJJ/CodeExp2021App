package com.example.codeexp2021app;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.codeexp2021app.base.BaseViewModel;
import com.example.codeexp2021app.network.http.ResponseCode;
import com.example.codeexp2021app.network.http.Result;

public class MainViewModel extends BaseViewModel {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {

    }

    /**
     * 处理网络请求结果的错误信息
     *
     * @param result
     */
    private void doResultErrorMsg(Result result) {
        if (result.getCode() == ResponseCode.NETWORK_ERROR) {

        } else if (result.getCode() == ResponseCode.NOT_FOUND) {

        } else {

        }
    }
}
