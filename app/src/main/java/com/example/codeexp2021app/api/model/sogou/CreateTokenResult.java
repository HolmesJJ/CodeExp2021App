package com.example.codeexp2021app.api.model.sogou;

import com.alibaba.fastjson.annotation.JSONField;

public class CreateTokenResult {

    private String token;
    @JSONField(name = "begin_time")
    private String beginTime;
    @JSONField(name = "end_time")
    private String endTime;

    public CreateTokenResult() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "CreateTokenParameter{" + "token='" + token + '\'' + '}';
    }
}
