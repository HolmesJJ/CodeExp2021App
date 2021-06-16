package com.example.codeexp2021app.config;

import androidx.core.content.ContextCompat;

import com.example.codeexp2021app.R;
import com.example.codeexp2021app.constants.SpUtilKeyConstants;
import com.example.codeexp2021app.constants.SpUtilValueConstants;
import com.example.codeexp2021app.utils.ContextUtils;
import com.example.codeexp2021app.utils.SpUtils;

public class Config {

    public static final String SETTING_CONFIG = "SettingConfig";
    public static String sToken;
    public static long sExpirationTime;
    public static int sFrontSize;
    public static String sFrontColor;
    public static int sVolume;
    public static int sVoiceType;
    public static boolean sIsShowEmoji;

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

    public static void setFrontSize(int frontSize) {
        sSp.put(SpUtilKeyConstants.FRONT_SIZE, frontSize);
        sFrontSize = frontSize;
    }

    public static void setFrontColor(String frontColor) {
        sSp.put(SpUtilKeyConstants.FRONT_COLOR, frontColor);
        sFrontColor = frontColor;
    }

    public static void setVolume(int volume) {
        sSp.put(SpUtilKeyConstants.VOLUME, volume);
        sVolume = volume;
    }

    public static void setVoiceType(int voiceType) {
        sSp.put(SpUtilKeyConstants.VOICE_TYPE, voiceType);
        sVoiceType = voiceType;
    }

    public static void setIsShowEmoji(boolean isShowEmoji) {
        sSp.put(SpUtilKeyConstants.IS_SHOW_EMOJI, isShowEmoji);
        sIsShowEmoji = isShowEmoji;
    }

    public static void resetConfig() {
        SpUtils.getInstance(SETTING_CONFIG).clear();
        loadConfig();
    }

    public static void loadConfig() {
        sToken = sSp.getString(SpUtilKeyConstants.TOKEN, "");
        sExpirationTime = sSp.getLong(SpUtilKeyConstants.EXPIRATION_TIME, -1);
        sFrontSize = sSp.getInt(SpUtilKeyConstants.FRONT_SIZE, 32);
        sFrontColor = sSp.getString(SpUtilKeyConstants.FRONT_COLOR,
                "#" + Integer.toHexString(ContextCompat.getColor(ContextUtils.getContext(), R.color.black)));
        sVolume = sSp.getInt(SpUtilKeyConstants.VOLUME, 8);
        sVoiceType = sSp.getInt(SpUtilKeyConstants.VOICE_TYPE, SpUtilValueConstants.NORMAL);
        sIsShowEmoji = sSp.getBoolean(SpUtilKeyConstants.IS_SHOW_EMOJI, false);
    }

    static {
        loadConfig();
    }
}
