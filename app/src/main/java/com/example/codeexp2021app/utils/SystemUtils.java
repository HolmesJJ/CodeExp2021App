package com.example.codeexp2021app.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统属性获取修改工具类
 */
public class SystemUtils {

    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    public static boolean isAppMainProcess(Context context) {
        try {
            int pid = android.os.Process.myPid();
            String process = getAppNameByPID(context.getApplicationContext(), pid);
            if (TextUtils.isEmpty(process)) {
                return true;
            } else if (context.getPackageName().equalsIgnoreCase(process)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 根据Pid得到进程名
     */
    public static String getAppNameByPID(Context context, int pid) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }

    /**
     * 获取当前应用已加载的SO库
     */
    public static String getSOLoaded() {
        // 当前应用的进程ID
        int pid = android.os.Process.myPid();
        String path = "/proc/" + pid + "/maps";
        File file = new File(path);
        if(file.exists() && file.isFile()) {
            return FileUtils.readFileByLines(file.getAbsolutePath());
        } else {
            return "不存在[" + path + "]文件.";
        }
    }
}
