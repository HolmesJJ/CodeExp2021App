package com.example.codeexp2021app.constants;

import android.media.AudioFormat;

public class Constants {

    public static final int PERMISSION_REQUEST_CODE = 101;
    public static final int AUDIO_CAPTURE_SERVICE_CHANNEL_ID = 102;

    public static final String AUDIO_CAPTURE_DIRECTORY = "AudioCaptures";
    public static final String AUDIO_CAPTURE_FILE = "AudioCapture.m4a";

    public static final String AUDIO_CAPTURE_SERVICE_CHANNEL = "AudioCaptureServiceChannel";
    public static final String AUDIO_CAPTURE_SERVICE_START = "AudioCaptureServiceStart";
    public static final String AUDIO_CAPTURE_SERVICE_STOP = "AudioCaptureServiceStop";

    // 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用
    public static final int SAMPLE_RATE_HERTZ = 44100;

    // 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    // 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    // sogou-asr
    public static final String SOGOU_API = "https://api.zhiyin.sogou.com/apis/";
    public static final String SOGOU_WSS = "wss://api.zhiyin.sogou.com/wss/";
    public static final String APP_ID = "1tvGVPG86SLG6I2CpUBwfbCGBLf";
    public static final String APP_KEY = "hNN4ii/gBgqFsMchfiw48rxTtz/nFK8UnoEF0156zQIpOpK28H/QUMWHPUA39VcLTdTHtpKA4Ig9Lo3DuZAMBQ==";

    public static final String SERVER_ADDRESS = "http://192.168.1.128:3000/";

    public Constants() {
    }
}
