package com.example.codeexp2021app.config;

import com.example.codeexp2021app.utils.SpUtils;

public class Config {

    public static final String SETTING_CONFIG = "SettingConfig";

    private static SpUtils sSp = SpUtils.getInstance(SETTING_CONFIG);

    public Config() {
    }

    public static void resetConfig() {
        SpUtils.getInstance(SETTING_CONFIG).clear();
        loadConfig();
    }

    public static void loadConfig() {
    }

    static {
        loadConfig();
    }
}
