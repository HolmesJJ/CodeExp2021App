package com.example.codeexp2021app.base;

import android.app.Application;

import com.example.codeexp2021app.api.LinkHelper;
import com.example.codeexp2021app.utils.ContextUtils;
import com.example.codeexp2021app.utils.FileUtils;
import com.example.codeexp2021app.utils.SystemUtils;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 多进程会调用多次onCreate方法，所以需要判断是否是主进程
        if (SystemUtils.isAppMainProcess(this)) {
            ContextUtils.init(this);
            FileUtils.init();
            LinkHelper.getInstance().setConnectTimeout(10000).setReadTimeout(30000).setWriteTimeout(10000).setDebug(true);
        }
    }
}
