package com.architecture.realarchitecture.domain;

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
    @GET("users/{user}/repos")
    Call<Map<String, Object>> listRepos(@Path("user") String user);
}
