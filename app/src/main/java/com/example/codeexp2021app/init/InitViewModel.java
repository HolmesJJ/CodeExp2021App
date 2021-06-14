package com.example.codeexp2021app.init;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.codeexp2021app.base.BaseViewModel;
import com.example.codeexp2021app.ui.activity.LoginActivity;

public class InitViewModel extends BaseViewModel {

    private static final String TAG = InitViewModel.class.getSimpleName();

    public MutableLiveData<Class> mActivityAction = new MutableLiveData<>();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {

    }

    public void initData() {
        doInitSuccess();
    }

    private void doInitSuccess() {
        mActivityAction.postValue(LoginActivity.class);
    }
}
