package com.example.codeexp2021app.network.http.interceptor;

import com.example.codeexp2021app.network.http.Request;
import com.example.codeexp2021app.network.http.Result;

import java.io.IOException;
import java.util.List;

/**
 * 拦截器内
 */
public class BaseChain implements Interceptor.Chain {

    private final List<Interceptor> mInterceptorList;
    private final int mIndex;
    private final Request mRequest;

    public BaseChain(List<Interceptor> interceptors, int index, Request request) {
        this.mInterceptorList = interceptors;
        this.mIndex = index;
        this.mRequest = request;
    }

    @Override
    public Request request() {
        return mRequest;
    }

    @Override
    public Result proceed(Request request) throws IOException {
        if (mIndex >= mInterceptorList.size()) {
            throw new AssertionError();
        }
        // Call the next interceptor in the chain.
        BaseChain next = new BaseChain(mInterceptorList, mIndex + 1, request);
        Interceptor interceptor = mInterceptorList.get(mIndex);
        Result response = interceptor.intercept(next);
        // Confirm that the intercepted response isn't null.
        if (response == null) {
            throw new NullPointerException("interceptor " + interceptor + " returned null");
        }
        return response;
    }
}
