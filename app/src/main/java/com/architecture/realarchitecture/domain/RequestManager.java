package com.architecture.realarchitecture.domain;

import android.text.TextUtils;

import com.architecture.realarchitecture.domain.request.Request;
import com.architecture.realarchitecture.utils.LogUtils;

import java.util.concurrent.Executor;

/**
 * Created by liushuo on 16/3/17.
 */
public class RequestManager {
    private RequestExecutor mRequestExecutor;

    private static RequestManager mInstance;

    private RequestManager() {
        mRequestExecutor = new RequestExecutor();
    }

    public static RequestManager getInstance() {
        synchronized (RequestManager.class) {
            if (mInstance == null) {
                mInstance = new RequestManager();
            }
        }
        return mInstance;
    }

    public synchronized void enqueueRequest(Request request) {
        if (request == null) return;

        RequestRunnable runnable = new RequestRunnable(request);
        mRequestExecutor.submitRequest(request.getRequestId(), runnable);
        LogUtils.d("running task count:" + mRequestExecutor.getActiveCount() + ",request:" + request.getClass().getSimpleName());
    }

    /**
     * request 在自定义线程池中运行，不能被取消
     *
     * @param executor
     * @param request
     */
    public void enqueueRequest(Executor executor, Request request) {
        if (request == null || executor == null) return;

        Runnable runnable = new RequestRunnable(request);
        executor.execute(runnable);

    }


    /**
     * 取消正在执行的或者正在排队的请求
     *
     * @param requestId
     */
    public void cancelRequest(String requestId) {
        if (!TextUtils.isEmpty(requestId)) {
            mRequestExecutor.cancelRequest(requestId);
        }
    }


    class RequestRunnable implements Runnable {
        Request request;

        public RequestRunnable(Request request) {
            this.request = request;
        }

        public Request getRequest() {
            return request;
        }

        @Override
        public void run() {
            request.perform();
        }
    }

}
