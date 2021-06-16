package com.example.codeexp2021app.utils;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarUtils {

    public static void updateStatusBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    public static void setStatusBarTextBlack(Activity activity) {
        Window window = activity.getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
