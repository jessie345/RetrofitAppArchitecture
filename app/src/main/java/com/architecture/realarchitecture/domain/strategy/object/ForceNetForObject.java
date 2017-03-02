package com.architecture.realarchitecture.domain.strategy.object;

import com.architecture.realarchitecture.datasource.base.MemoryStorage;
import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.datasource.sync.NoSqlSyncStrategy;
import com.architecture.realarchitecture.datasource.DALFactory;
import com.architecture.realarchitecture.domain.ResponseListener;
import com.architecture.realarchitecture.domain.strategy.base.ObjectGetStrategy;

import java.util.Map;

import retrofit2.Call;

/**
 * Created by liushuo on 16/3/17.
 * 检测到请求未过期，使用三级缓存数据获取策略
 */
public class ForceNetForObject extends ObjectGetStrategy {
    private MemoryStorage mMemoryDataPool;

    public ForceNetForObject(String dataType, String id, Call<Map<String, Object>> call, ResponseListener<Map<String, Object>> listener) {
        super(dataType, id, call, listener);
        mMemoryDataPool = DALFactory.getMemoryStorage().configureSyncStrategy(NoSqlSyncStrategy.mStrategy);
    }


    @Override
    public void fetchData() {
        Map<String, Object> data = mMemoryDataPool.getObjectDataForId(mDataType, mId);

        if (data != null) {

            notifyCacheSuccess(data, false);

        }

        //执行网络之前，回调reqeust
        if (mResponseListener != null) {
            mResponseListener.preNetRequest();
        }

        invokeRetrofit();

    }

    private void notifyCacheSuccess(Map<String, Object> data, boolean isDone) {
        if (mResponseListener != null) {
            mResponseListener.onCacheResponse(data, isDone);
        }
    }
}
