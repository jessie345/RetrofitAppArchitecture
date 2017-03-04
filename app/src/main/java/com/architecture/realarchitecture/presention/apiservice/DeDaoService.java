package com.architecture.realarchitecture.presention.apiservice;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by liushuo on 2017/3/2.
 * <p>
 * 服务器返回的任何数据 都是一个map结构
 */

public interface DeDaoService {
    @GET("adv/latest")
    Call<Map<String, Object>> testGetObject();

    @GET("columnnote/alllist")
    Call<Map<String, Object>> testGetList();
}
