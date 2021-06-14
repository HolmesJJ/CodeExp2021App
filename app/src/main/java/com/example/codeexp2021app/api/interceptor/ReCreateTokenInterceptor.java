package com.example.codeexp2021app.api.interceptor;

import androidx.annotation.NonNull;

import com.example.codeexp2021app.api.ApiClient;
import com.example.codeexp2021app.api.model.sogou.CreateTokenResult;
import com.example.codeexp2021app.constants.Constants;
import com.example.codeexp2021app.network.http.Request;
import com.example.codeexp2021app.network.http.ResponseCode;
import com.example.codeexp2021app.network.http.Result;
import com.example.codeexp2021app.network.http.interceptor.Interceptor;

import java.io.IOException;
import java.time.Duration;

/**
 * 头部信息拦截器，负责在请求头部加入固定的参数
 */
public class ReCreateTokenInterceptor implements Interceptor {

    @NonNull
    @Override
    public Result intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Result result = null;
        try {
            result = chain.proceed(request);
        } catch (Exception e) {
            e.getStackTrace();
        }
        // 如果请求失败，且是因为token过期导致的则自动重新获取新的token
        if (!result.isSuccess() && result.getCode() == ResponseCode.TOKEN_TIMEOUT) {
            Result<CreateTokenResult> createTokenResult = ApiClient.createToken(Constants.APP_ID, Constants.APP_KEY, Duration.ofHours(1));
            if (createTokenResult.isSuccess()) {
                result = null;
                try {
                    result = chain.proceed(request);
                } catch (Exception e) {
                    e.getStackTrace();
                }
            } else {
                // 如果获取失败，则将获取失败的结果返回出去
                result.setCode(createTokenResult.getCode())
                        .setMessage(createTokenResult.getMessage())
                        .setDesc(createTokenResult.getDesc())
                        .setData(null)
                        .setOriginData(null)
                        .setRawData(null);
            }
        }
        return result;
    }
}
