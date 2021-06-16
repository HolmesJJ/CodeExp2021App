package com.example.codeexp2021app.listener;

import android.media.AudioFormat;

public interface AudioRecordListener {

    /**
     * Called when the recorder starts hearing voice.
     */
    public void onAudioStart();

    /**
     * Called when the recorder is hearing voice.
     *
     * @param data The audio data in {@link AudioFormat#ENCODING_PCM_16BIT}.
     * @param size The size of the actual data in {@code data}.
     */
    public void onAudio(byte[] data, int size);

    /**
     * Called when the recorder stops hearing voice.
     */
    public void onAudioEnd();
}
