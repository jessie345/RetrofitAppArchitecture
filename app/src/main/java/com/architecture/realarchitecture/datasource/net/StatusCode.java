package com.architecture.realarchitecture.datasource.net;

/**
 * Created by liushuo on 2017/3/3.
 */

public interface StatusCode {
    /**
     * 定义http响应的状态码
     */
    public interface Http {
        int HTTP_OK = 200;//服务器返回相应码
    }

    /**
     * 定义Server 响应的状态码
     */
    public interface Server {
        int SERVER_SUCCESS = 0;
    }
}
