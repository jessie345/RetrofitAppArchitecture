package com.architecture.realarchitecture.domain;

import com.architecture.realarchitecture.datasource.net.ResponseHeader;

import java.util.Map;

/**
 * Created by liushuo on 16/3/17.
 */
public interface ResponseListener<T> {

    void onCacheResponse(T t, boolean isDone);

    /**
     * 执行网络请求之前策略需要回调接口通知request执行相应操作(eg.弹窗加载对话框)
     */
    void preNetRequest();

    void onRetrofitResponse(ResponseHeader rb, Map<String, Object> header, Map<String, Object> content);


    void onNetRequestError(ResponseHeader httpResponse);
}
