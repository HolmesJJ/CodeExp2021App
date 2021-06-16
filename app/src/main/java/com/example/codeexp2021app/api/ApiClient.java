package com.example.codeexp2021app.api;

import androidx.annotation.NonNull;

import com.example.codeexp2021app.R;
import com.example.codeexp2021app.api.model.login.LoginParameter;
import com.example.codeexp2021app.api.model.login.LoginResult;
import com.example.codeexp2021app.api.model.google.CreateTokenResult;
import com.example.codeexp2021app.constants.Constants;
import com.example.codeexp2021app.network.http.Request;
import com.example.codeexp2021app.network.http.ResponseCode;
import com.example.codeexp2021app.network.http.Result;
import com.example.codeexp2021app.utils.ContextUtils;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;

/**
 * 标准http接口请求管理类
 */
public class ApiClient {



    // ***** WARNING *****
    // In this sample, we load the credential from a JSON file stored in a raw resource
    // folder of this client app. You should never do this in your app. Instead, store
    // the file in your server and obtain an access token from there.
    // *******************
    @NonNull
    public static Result<CreateTokenResult> createToken() {
        final InputStream stream = ContextUtils.getContext().getResources().openRawResource(R.raw.credential);
        try {
            final GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Constants.SCOPE);
            final AccessToken token = credentials.refreshAccessToken();
            CreateTokenResult createTokenResult = new CreateTokenResult();
            createTokenResult.setToken(token.getTokenValue());
            createTokenResult.setExpirationTime(token.getExpirationTime().getTime());
            Result<CreateTokenResult> createTokenResultResult = new Result<CreateTokenResult>();
            createTokenResultResult.setCode(ResponseCode.SUCCESS);
            createTokenResultResult.setData(createTokenResult);
            return createTokenResultResult;
        } catch (IOException e) {
            Result<CreateTokenResult> createTokenResultResult = new Result<CreateTokenResult>();
            createTokenResultResult.setCode(ResponseCode.NETWORK_ERROR);
            return createTokenResultResult;
        }
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

