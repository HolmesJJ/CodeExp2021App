package com.example.codeexp2021app.thread;

import android.os.Handler;
import android.os.Looper;

public class MainThreadImpl {
    private Handler mainThreadHandler;

    private MainThreadImpl() {
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    private static Handler get() {
        return MainThreadImpl.Instance.INSTANCE.mainThreadHandler;
    }

    public static void remove(Runnable command) {
        get().removeCallbacks(command);
    }

    public static void post(Runnable command) {
        get().post(command);
    }

    public static void post(Runnable command, long delayMillis) {
        get().postDelayed(command, delayMillis);
    }

    private static final class Instance {

        private static final MainThreadImpl INSTANCE = new MainThreadImpl();

        private Instance() {
        }
    }
}
