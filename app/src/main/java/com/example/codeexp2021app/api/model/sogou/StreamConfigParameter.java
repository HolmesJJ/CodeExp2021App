package com.example.codeexp2021app.api.model.sogou;

import com.alibaba.fastjson.annotation.JSONField;

public class StreamConfigParameter {

    @JSONField(name = "config")
    private AudioConfigParameter audioConfigParameter;
    @JSONField(name = "interim_results")
    private boolean interimResults;

    public StreamConfigParameter() {
    }

    public AudioConfigParameter getAudioConfigParameter() {
        return audioConfigParameter;
    }

    public StreamConfigParameter setAudioConfigParameter(AudioConfigParameter audioConfigParameter) {
        this.audioConfigParameter = audioConfigParameter;
        return this;
    }

    public boolean isInterimResults() {
        return interimResults;
    }

    public StreamConfigParameter setInterimResults(boolean interimResults) {
        this.interimResults = interimResults;
        return this;
    }

    @Override
    public String toString() {
        return "StreamConfigParameter{" + "audioConfigParameter='" + audioConfigParameter.toString() + '\'' + ", interimResults='" + interimResults + '\'' + '}';
    }
}
