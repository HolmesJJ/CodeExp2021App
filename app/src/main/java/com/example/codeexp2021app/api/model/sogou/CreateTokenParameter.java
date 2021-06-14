package com.example.codeexp2021app.api.model.sogou;

import com.alibaba.fastjson.annotation.JSONField;

public class CreateTokenParameter {

    @JSONField(name = "appid")
    private String appId;
    @JSONField(name = "appkey")
    private String appKey;
    @JSONField(name = "exp")
    private String duration;

    public CreateTokenParameter() {
    }

    public String getAppId() {
        return appId;
    }

    public CreateTokenParameter setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getAppKey() {
        return appKey;
    }

    public CreateTokenParameter setAppKey(String appKey) {
        this.appKey = appKey;
        return this;
    }

    public String getDuration() {
        return duration;
    }

    public CreateTokenParameter setDuration(String duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public String toString() {
        return "CreateTokenParameter{" + "appId='" + appId + '\'' + ", appKey='" + appKey +
                '\'' + ", duration='" + duration + '\'' + '}';
    }
}
