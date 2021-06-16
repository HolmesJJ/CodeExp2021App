package com.example.codeexp2021app.constants;

import java.util.Collections;
import java.util.List;

public class Constants {

    public static final int PERMISSION_REQUEST_CODE = 101;
    public static final int AUDIO_CAPTURE_SERVICE_CHANNEL_ID = 102;

    public static final String AUDIO_CAPTURE_DIRECTORY = "AudioCaptures";
    public static final String AUDIO_CAPTURE_FILE = "AudioCapture.m4a";

    public static final String AUDIO_CAPTURE_SERVICE_CHANNEL = "AudioCaptureServiceChannel";
    public static final String AUDIO_CAPTURE_SERVICE_START = "AudioCaptureServiceStart";
    public static final String AUDIO_CAPTURE_SERVICE_STOP = "AudioCaptureServiceStop";

    public static final String SERVER_ADDRESS = "http://192.168.1.128:3000/";

    public static final String GOOGLE_API_ADDRESS = "https://www.googleapis.com/auth/cloud-platform";
    public static final List<String> SCOPE = Collections.singletonList(Constants.GOOGLE_API_ADDRESS);
    public static final String HOSTNAME = "speech.googleapis.com";
    public static final int PORT = 443;

    public Constants() {
    }
}
