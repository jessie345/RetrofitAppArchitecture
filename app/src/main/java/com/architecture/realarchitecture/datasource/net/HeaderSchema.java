package com.architecture.realarchitecture.datasource.net;

/**
 * Created by liushuo on 2017/3/3.
 */

public interface HeaderSchema {
    String code = "c";//服务器返回码
    String error_msg = "e";//服务器返回错误信息
    String time_consume = "t";//服务器处理请求的耗时
    String server_timestamp = "s";//服务器时间戳
}
