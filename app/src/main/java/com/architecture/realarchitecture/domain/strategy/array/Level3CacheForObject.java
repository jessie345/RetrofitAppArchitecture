package com.architecture.realarchitecture.domain.strategy.array;

import com.architecture.realarchitecture.datasource.DALFactory;
import com.architecture.realarchitecture.datasource.base.MemoryStorage;
import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.datasource.sync.NoSqlSyncStrategy;
import com.architecture.realarchitecture.domain.ResponseListener;
import com.architecture.realarchitecture.domain.strategy.base.ArrayGetStrategy;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by liushuo on 16/3/17.
 * 检测到请求未过期，使用三级缓存数据获取策略
 */
public class Level3CacheForObject extends ArrayGetStrategy {
    private MemoryStorage mMemoryDataPool;

    public Level3CacheForObject(String dataType, Call<Map<String, Object>> call, ResponseListener<List<Map<String, Object>>> callback) {
        super(dataType, call, callback);

        mMemoryDataPool = DALFactory.getMemoryStorage().configureSyncStrategy(NoSqlSyncStrategy.mStrategy);
    }


    @Override
    public void fetchData() {
        List<Map<String, Object>> data = mMemoryDataPool.getArrayDatasCached(mDataType);

        if (data != null) {

            notifyCacheSuccess(data, true);

        } else {
            //执行网络之前，回调reqeust
            if (mResponseListener != null) {
                mResponseListener.preNetRequest();
            }

            invokeRetrofit();
        }

    }

    private void notifyCacheSuccess(List<Map<String, Object>> data, boolean isDone) {
        if (mResponseListener != null) {
            mResponseListener.onCacheResponse(data, isDone);
        }
    }

}
