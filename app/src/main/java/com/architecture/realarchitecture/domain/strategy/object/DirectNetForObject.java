package com.architecture.realarchitecture.domain.strategy.object;

import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.domain.ResponseListener;
import com.architecture.realarchitecture.domain.strategy.base.ObjectGetStrategy;

import java.util.Map;

import retrofit2.Call;

/**
 * Created by liushuo on 16/3/17.
 * 检测到请求未过期，使用三级缓存数据获取策略
 */
public class DirectNetForObject extends ObjectGetStrategy {

    public DirectNetForObject(Call<Map<String, Object>> call, ResponseListener<Map<String, Object>> callback) {
        super(null, null, call, callback);
    }


    @Override
    public void fetchData() {

        //执行网络之前，回调reqeust
        if (mResponseListener != null) {
            mResponseListener.preNetRequest();
        }

        invokeRetrofit();
    }
}