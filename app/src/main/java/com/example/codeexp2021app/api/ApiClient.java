package com.example.codeexp2021app.api;

import androidx.annotation.NonNull;

import com.example.codeexp2021app.api.model.login.LoginParameter;
import com.example.codeexp2021app.api.model.login.LoginResult;
import com.example.codeexp2021app.api.model.sogou.CreateTokenParameter;
import com.example.codeexp2021app.api.model.sogou.CreateTokenResult;
import com.example.codeexp2021app.constants.Constants;
import com.example.codeexp2021app.network.http.Request;
import com.example.codeexp2021app.network.http.Result;

import java.time.Duration;

/**
 * 标准http接口请求管理类
 */
public class ApiClient {

    @NonNull
    public static Result<CreateTokenResult> createToken(String appId, String appKey, Duration exp) {
        CreateTokenParameter createTokenParameter = new CreateTokenParameter();
        createTokenParameter.setAppId(appId);
        createTokenParameter.setAppKey(appKey);
        createTokenParameter.setDuration(String.format("%ds", exp.getSeconds()));
        Request request = new Request().setPath(Constants.SOGOU_API + "auth/v1/create_token")
                .setMethod(Request.RequestMethod.POST.value())
                .setBody(createTokenParameter);
        return ExecutorRequest.execute(request);
    }

    @NonNull
    public static Result<LoginResult> login(String username, String password) {
        LoginParameter loginParameter = new LoginParameter();
        loginParameter.setUsername(username);
        loginParameter.setPassword(password);
        Request request = new Request().setPath(Constants.SERVER_ADDRESS + "login")
                .setMethod(Request.RequestMethod.POST.value())
                .setBody(loginParameter);
        return ExecutorRequest.execute(request);
    }
}

