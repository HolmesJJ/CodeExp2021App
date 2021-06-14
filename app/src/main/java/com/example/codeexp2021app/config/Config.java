package com.example.codeexp2021app.config;

import com.example.codeexp2021app.constants.SpUtilKeyConstants;
import com.example.codeexp2021app.utils.SpUtils;


public class Config {

    public static final String SETTING_CONFIG = "SettingConfig";
    public static String sSogouToken;

    private static SpUtils sSp = SpUtils.getInstance(SETTING_CONFIG);

    public Config() {
    }

    public static void setSogouToken(String sogouToken) {
        sSp.put(SpUtilKeyConstants.SOGOU_TOKEN, sogouToken);
        sSogouToken = sogouToken;
    }

    public static void resetConfig() {
        SpUtils.getInstance(SETTING_CONFIG).clear();
        loadConfig();
    }

    public static void loadConfig() {
        sSogouToken = sSp.getString(SpUtilKeyConstants.SOGOU_TOKEN, "");
    }

    static {
        loadConfig();
    }
}
