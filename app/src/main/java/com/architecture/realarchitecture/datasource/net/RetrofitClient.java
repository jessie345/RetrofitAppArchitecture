package com.architecture.realarchitecture.datasource.net;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Created by liushuo on 2017/3/2.
 */

public class RetrofitClient {
    private static Retrofit retrofit;

    private static Map<Class, Object> sServiceCache = new HashMap<>();

    private RetrofitClient() {
    }

    public static <T> T getApiService(Class<T> cls) {
        synchronized (RetrofitClient.class) {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.github.com/")
                        .build();
            }
        }

        synchronized (sServiceCache) {
            if (sServiceCache.get(cls) == null) {
                sServiceCache.put(cls, retrofit.create(cls));
            }
        }

        return (T) sServiceCache.get(cls);


    }
}


