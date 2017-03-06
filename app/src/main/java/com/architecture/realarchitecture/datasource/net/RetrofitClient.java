package com.architecture.realarchitecture.datasource.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by liushuo on 2017/3/2.
 */

public class RetrofitClient {
    private static Retrofit sRetrofit;

    private static Map<Class, Object> sServiceCache = new HashMap<>();

    private RetrofitClient() {
    }

    public static <T> T getApiService(Class<T> cls) {
        synchronized (RetrofitClient.class) {
            if (sRetrofit == null) {

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = configureCommonHeaders(chain);
                                return chain.proceed(request);
                            }

                        })
                        .build();

                sRetrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.100.30:9999")
                        .client(okHttpClient)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build();
            }
        }

        synchronized (sServiceCache) {
            if (sServiceCache.get(cls) == null) {
                sServiceCache.put(cls, sRetrofit.create(cls));
            }
        }

        return (T) sServiceCache.get(cls);


    }

    private static Request configureCommonHeaders(Interceptor.Chain chain) {
        Request request = chain.request()
                .newBuilder()
                .cacheControl(CacheControl.FORCE_NETWORK)
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "*/*")
                .addHeader("Cookie", "add cookies here")
                .build();

        return request;
    }
}


