package com.architecture.realarchitecture.datasource;

import android.content.Context;

import com.architecture.realarchitecture.datasource.base.AdvanceLocalStorage;
import com.architecture.realarchitecture.datasource.base.BaseLocalStorage;
import com.architecture.realarchitecture.datasource.base.MemoryStorage;
import com.architecture.realarchitecture.datasource.database.NosqlStorage;
import com.architecture.realarchitecture.datasource.database.SqlStorage;
import com.architecture.realarchitecture.datasource.net.RetrofitClient;

/**
 * Created by liushuo on 16/4/5.
 * data abstract layer factory(数据源层访问工厂),用于获取对应的访问实例,具体实现细节，参考相应的具体实现
 * 使用之前必须执行init初始化操作
 */
public class DALFactory {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    /**
     * 获取内存池实例，内存池可以配置数据同步方案（内存访问失效需要指定是否同步本地数据及同步方式）
     *
     * @return
     */
    public static MemoryStorage getMemoryStorage() {

        return MemoryDataPool.getInstance();
    }

    /**
     * 获取基本数据存储服务，nosql实现，提供基本数据查询方式（基于_id|全表查询）
     *
     * @return
     */
    public static BaseLocalStorage getBaseStorage() {
        checkContext();

        return NosqlStorage.getInstance(mContext);

    }

    /**
     * 获取高级数据存储服务，sql实现，提供数据查询方式的灵活性（基于_id|全表查询|自定义column查询）
     *
     * @return
     */
    public static AdvanceLocalStorage getAdvanceStorage() {
        checkContext();

        return SqlStorage.getInstance(mContext);
    }

    public static <T> T getApiService(Class<T> cls) {
        return RetrofitClient.getApiService(cls);
    }


    private static void checkContext() {
        if (mContext == null)
            throw new RuntimeException("has not attach app context,should invoke init() in application oncreate()");
    }
}
