package com.architecture.realarchitecture.domain.strategy.base;

import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.domain.ResponseListener;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by liushuo on 16/3/17.
 * the object is json object
 */
public abstract class ArrayGetStrategy extends RetrofitStrategy {
    protected ResponseListener<List<Map<String, Object>>> mResponseListener;
    protected String mDataType;

    public ArrayGetStrategy(String dataType, Call<Map<String, Object>> call, ResponseListener<List<Map<String, Object>>> listener) {
        super(call);
        this.mResponseListener = listener;
        mDataType = dataType;
    }

    @Override
    protected void notifyNetSuccess(ResponseHeader rh, Map<String, Object> header, Map<String, Object> content) {
        if (mResponseListener != null) {
            mResponseListener.onRetrofitResponse(rh, header, content);
        }
    }

    @Override
    protected void notifyNetError(ResponseHeader rb) {
        if (mResponseListener != null) {
            mResponseListener.onNetRequestError(rb);
        }
    }

    public abstract void fetchData();
}
