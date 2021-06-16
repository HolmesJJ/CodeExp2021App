package com.example.codeexp2021app.ui.viewmodel;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.codeexp2021app.base.BaseViewModel;

public class BluetoothViewModel extends BaseViewModel {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {

    }
}
