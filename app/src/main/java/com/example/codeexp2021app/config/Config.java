package com.example.codeexp2021app.config;

import com.example.codeexp2021app.constants.SpUtilKeyConstants;
import com.example.codeexp2021app.utils.SpUtils;


public class Config {

    public static final String SETTING_CONFIG = "SettingConfig";
    public static String sToken;
    public static long sExpirationTime;

    private static SpUtils sSp = SpUtils.getInstance(SETTING_CONFIG);

    public Config() {
    }

    public static void setToken(String token) {
        sSp.put(SpUtilKeyConstants.TOKEN, token);
        sToken = token;
    }

    public static void setExpirationTime(long expirationTime) {
        sSp.put(SpUtilKeyConstants.EXPIRATION_TIME, expirationTime);
        sExpirationTime = expirationTime;
    }

    public static void resetConfig() {
        SpUtils.getInstance(SETTING_CONFIG).clear();
        loadConfig();
    }

    public static void loadConfig() {
        sToken = sSp.getString(SpUtilKeyConstants.TOKEN, "");
        sExpirationTime = sSp.getLong(SpUtilKeyConstants.EXPIRATION_TIME, -1);
    }

    static {
        loadConfig();
    }
}
