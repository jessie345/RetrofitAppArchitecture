package com.architecture.realarchitecture.domain.strategy.base;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;

import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.datasource.net.ResponseSchema;
import com.architecture.realarchitecture.datasource.net.StatusCode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by liushuo on 16/3/17.
 * the object is json object
 */
public abstract class RetrofitStrategy {
    private Call<Map<String, Object>> mCall;


    public RetrofitStrategy(@NonNull Call<Map<String, Object>> call) {
        mCall = call;
    }

    protected void invokeRetrofit() {
        Response<Map<String, Object>> response = null;
        try {
            //when canceled ,it throws IOException("canceled")
            response = mCall.execute();

            //网络数据返回调用者
            ResponseHeader rb = new ResponseHeader(response.code(), response.message());
            Pair<Map<String, Object>, Map<String, Object>> pair = parseHeaderContent(response.body());
            dispatchRetrofitResponse(rb, pair.first, pair.second);
        } catch (Exception e) {
            e.printStackTrace();

            if (TextUtils.equals("canceled", e.getMessage())) {//请求被取消
                dispatchRetrofitResponse(new ResponseHeader(StatusCode.Http.HTTP_CANCELED, e.getMessage()), null, null);
            } else {
                //请求执行过程中，发生异常
                dispatchRetrofitResponse(new ResponseHeader(-1, e.getMessage()), null, null);
            }
        }
    }

    private Pair<Map<String, Object>, Map<String, Object>> parseHeaderContent(Map<String, Object> resp) {
        if (resp == null) return Pair.create(null, null);

        return Pair.create((Map<String, Object>) (resp.get(ResponseSchema.header)), (Map<String, Object>) (resp.get(ResponseSchema.content)));
    }

    public void dispatchRetrofitResponse(ResponseHeader rh, Map<String, Object> header, Map<String, Object> content) {
        if (header != null) header = new ConcurrentHashMap<>(header);
        if (header == null) header = new ConcurrentHashMap<>();//返回客户端安全类型

        if (content != null) content = new ConcurrentHashMap<>(content);
        if (content == null) content = new ConcurrentHashMap<>();//返回客户端安全类型

        if (rh.getCode() == StatusCode.Http.HTTP_OK) {
            notifyNetSuccess(rh, header, content);//网络数据返回，认为请求已经执行完成
        } else if (rh.getCode() == StatusCode.Http.HTTP_CANCELED) {
            notifyNetCanceled(rh);
        } else {
            notifyNetError(rh);
        }
    }

    protected abstract void notifyNetSuccess(ResponseHeader rh, Map<String, Object> header, Map<String, Object> content);

    protected abstract void notifyNetError(ResponseHeader rb);

    protected abstract void notifyNetCanceled(ResponseHeader rb);

}
