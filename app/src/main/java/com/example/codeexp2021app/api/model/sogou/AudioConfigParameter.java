package com.example.codeexp2021app.api.model.sogou;

import com.alibaba.fastjson.annotation.JSONField;

public class AudioConfigParameter {

    private String encoding;
    @JSONField(name = "sample_rate_hertz")
    private int sampleRateHertz;
    @JSONField(name = "language_code")
    private String languageCode;

    public AudioConfigParameter() {
    }

    public String getEncoding() {
        return encoding;
    }

    public AudioConfigParameter setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public int getSampleRateHertz() {
        return sampleRateHertz;
    }

    public AudioConfigParameter setSampleRateHertz(int sampleRateHertz) {
        this.sampleRateHertz = sampleRateHertz;
        return this;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public AudioConfigParameter setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
        return this;
    }

    @Override
    public String toString() {
        return "AudioConfigParameter{" + "encoding='" + encoding + '\'' + ", sampleRateHertz='" + sampleRateHertz +
                '\'' + ", languageCode='" + languageCode + '\'' + '}';
    }
}
