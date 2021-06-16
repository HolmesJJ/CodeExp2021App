package com.example.codeexp2021app.constants;

import android.media.AudioFormat;

import java.util.Collections;
import java.util.List;

public class Constants {

    public static final int PERMISSION_REQUEST_CODE = 101;
    public static final int AUDIO_CAPTURE_SERVICE_CHANNEL_ID = 102;

    public static final String AUDIO_CAPTURE_DIRECTORY = "AudioCaptures";
    public static final String AUDIO_CAPTURE_PCM = "AudioCapture.pcm";
    public static final String AUDIO_CAPTURE_WAV = "AudioCapture.wav";

    public static final String AUDIO_CAPTURE_SERVICE_CHANNEL = "AudioCaptureServiceChannel";
    public static final String AUDIO_CAPTURE_SERVICE_START = "AudioCaptureServiceStart";
    public static final String AUDIO_CAPTURE_SERVICE_STOP = "AudioCaptureServiceStop";

    public static final String SERVER_ADDRESS = "http://192.168.1.128:3000/";

    public static final String GOOGLE_API_ADDRESS = "https://www.googleapis.com/auth/cloud-platform";
    public static final List<String> SCOPE = Collections.singletonList(Constants.GOOGLE_API_ADDRESS);
    public static final String HOSTNAME = "speech.googleapis.com";
    public static final int PORT = 443;

    public static final int[] SAMPLE_RATE_CANDIDATES = new int[]{16000, 11025, 22050, 44100};

    public static final int CHANNEL_IN = AudioFormat.CHANNEL_IN_MONO;
    public static final int CHANNEL_OUT = AudioFormat.CHANNEL_OUT_MONO;
    public static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public Constants() {
    }
}
