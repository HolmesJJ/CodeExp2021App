package com.example.codeexp2021app.media;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.codeexp2021app.constants.Constants;
import com.example.codeexp2021app.listener.AudioRecordListener;
import com.example.codeexp2021app.utils.ContextUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Continuously records audio and notifies the {@link AudioRecordListener} when voice (or any
 * sound) is heard.
 *
 * <p>The recorded audio format is always {@link AudioFormat#ENCODING_PCM_16BIT} and
 * {@link AudioFormat#CHANNEL_IN_MONO}. This class will automatically pick the right sample rate
 * for the device. Use {@link #getSampleRate()} to get the selected value.</p>
 */
public class AudioRecordHelper {

    private static final int AMPLITUDE_THRESHOLD = 1500;
    private static final int SPEECH_TIMEOUT_MILLIS = 2000;
    private static final int MAX_SPEECH_LENGTH_MILLIS = 30 * 1000;

    private volatile boolean isProcessing;

    private AudioRecordListener mAudioRecordListener;

    private AudioRecordHelper() {
    }

    private static class SingleInstance {
        private static AudioRecordHelper INSTANCE = new AudioRecordHelper();
    }

    public static AudioRecordHelper getInstance() {
        return AudioRecordHelper.SingleInstance.INSTANCE;
    }

    public void init(AudioRecordListener mAudioRecordListener) {
        this.mAudioRecordListener = mAudioRecordListener;
    }

    private final Object mLock = new Object();
    private AudioRecord mAudioRecord;
    private AudioTrack mAudioTrack;
    private AudioManager mAudioManager;
    private byte[] mBuffer;
    private int mBufferSize;

    /** The timestamp of the last time that voice is heard. */
    private long mLastVoiceHeardMillis = Long.MAX_VALUE;

    /** The timestamp when the current voice is started. */
    private long mVoiceStartedMillis;

    /**
     * Starts recording audio.
     *
     * The caller is responsible for calling {@link #stop()} later.</p>
     */
    public void start() {
        // Stop recording if it is currently ongoing.
        stop();
        // Try to create a new recording session.
        mAudioRecord = createAudioRecord();
        if (mAudioRecord == null) {
            throw new RuntimeException("Cannot instantiate AudioRecorder");
        }
        mAudioTrack = createAudioTrack();
        initAudioManager();
        // Start recording.
        mAudioRecord.startRecording();
        mAudioTrack.play();
    }

    /**
     * Stops recording audio.
     */
    public void stop() {
        isProcessing = false;
        synchronized (mLock) {
            dismiss();
            if (mAudioRecord != null) {
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
            if (mAudioTrack != null) {
                mAudioTrack.pause();
//                mAudioTrack.release();
//                mAudioTrack = null;
            }
            mBuffer = null;
        }
    }

    public void release() {
        stop();
        mAudioRecordListener = null;
    }

    /**
     * Dismisses the currently ongoing utterance.
     */
    public void dismiss() {
        if (mLastVoiceHeardMillis != Long.MAX_VALUE) {
            mLastVoiceHeardMillis = Long.MAX_VALUE;
            if (mAudioRecordListener != null) {
                mAudioRecordListener.onAudioEnd();
            }
        }
    }

    /**
     * Retrieves the sample rate currently used to record audio.
     *
     * @return The sample rate of recorded audio.
     */
    public int getSampleRate() {
        if (mAudioRecord != null) {
            return mAudioRecord.getSampleRate();
        }
        return 0;
    }

    /**
     * Creates a new {@link AudioRecord}.
     *
     * @return A newly created {@link AudioRecord}, or null if it cannot be created (missing
     * permissions?).
     */
    private AudioRecord createAudioRecord() {
        for (int sampleRate : Constants.SAMPLE_RATE_CANDIDATES) {
            final int sizeInBytes = AudioRecord.getMinBufferSize(sampleRate, Constants.CHANNEL_IN, Constants.ENCODING);
            if (sizeInBytes == AudioRecord.ERROR_BAD_VALUE) {
                continue;
            }
            final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    sampleRate, Constants.CHANNEL_IN, Constants.ENCODING, sizeInBytes);
            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                mBufferSize = sizeInBytes;
                mBuffer = new byte[mBufferSize];
                return audioRecord;
            } else {
                audioRecord.release();
            }
        }
        return null;
    }

    private AudioTrack createAudioTrack() {
        return mAudioTrack = new AudioTrack(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                new AudioFormat.Builder()
                        .setSampleRate(getSampleRate())
                        .setEncoding(Constants.ENCODING)
                        .setChannelMask(Constants.CHANNEL_OUT).build(),
                mBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);
    }

    private void initAudioManager() {
        mAudioManager = (AudioManager) ContextUtils.getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setSpeakerphoneOn(true);
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    /**
     * Continuously processes the captured audio and notifies {@link #mAudioRecordListener} of corresponding
     * events.
     */
    public void process() {
        isProcessing = true;
        while (isProcessing) {
            synchronized (mLock) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                final int size = mAudioRecord.read(mBuffer, 0, mBuffer.length);
                mAudioTrack.write(mBuffer, 0, size);
                final long now = System.currentTimeMillis();
                if (isHearingVoice(mBuffer, size)) {
                    if (mLastVoiceHeardMillis == Long.MAX_VALUE) {
                        mVoiceStartedMillis = now;
                        if (mAudioRecordListener != null) {
                            mAudioRecordListener.onAudioStart();
                        }
                    }
                    if (mAudioRecordListener != null) {
                        mAudioRecordListener.onAudio(mBuffer, size);
                    }
                    mLastVoiceHeardMillis = now;
                    if (now - mVoiceStartedMillis > MAX_SPEECH_LENGTH_MILLIS) {
                        mLastVoiceHeardMillis = Long.MAX_VALUE;
                        if (mAudioRecordListener != null) {
                            mAudioRecordListener.onAudioEnd();
                        }
                    }
                } else if (mLastVoiceHeardMillis != Long.MAX_VALUE) {
                    if (mAudioRecordListener != null) {
                        mAudioRecordListener.onAudio(mBuffer, size);
                    }
                    if (now - mLastVoiceHeardMillis > SPEECH_TIMEOUT_MILLIS) {
                        mLastVoiceHeardMillis = Long.MAX_VALUE;
                        if (mAudioRecordListener != null) {
                            mAudioRecordListener.onAudioEnd();
                        }
                    }
                }
                if (size > 0) {
                    if (mAudioRecordListener != null) {
                        mAudioRecordListener.onFile(mBuffer, size);
                    }
                }
            }
        }
    }

    private boolean isHearingVoice(byte[] buffer, int size) {
        for (int i = 0; i < size - 1; i += 2) {
            // The buffer has LINEAR16 in little endian.
            int s = buffer[i + 1];
            if (s < 0) s *= -1;
            s <<= 8;
            s += Math.abs(buffer[i]);
            if (s > AMPLITUDE_THRESHOLD) {
                return true;
            }
        }
        return false;
    }
}
