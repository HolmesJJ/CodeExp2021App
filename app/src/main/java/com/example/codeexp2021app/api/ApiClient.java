package com.example.codeexp2021app.api;

import androidx.annotation.NonNull;

import com.example.codeexp2021app.api.model.LoginParameter;
import com.example.codeexp2021app.api.model.LoginResult;
import com.example.codeexp2021app.constants.Constants;
import com.example.codeexp2021app.network.http.Request;
import com.example.codeexp2021app.network.http.Result;

/**
 * 标准http接口请求管理类
 */
public class ApiClient {

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

