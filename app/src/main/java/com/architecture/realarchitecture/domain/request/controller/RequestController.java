package com.architecture.realarchitecture.domain.request.controller;

import android.text.TextUtils;

import com.architecture.realarchitecture.domain.request.Request;
import com.architecture.realarchitecture.domain.RequestManager;
import com.architecture.realarchitecture.domain.eventbus.EventRequestCanceled;
import com.architecture.realarchitecture.domain.eventbus.EventNetError;
import com.architecture.realarchitecture.domain.eventbus.EventPreNetRequest;
import com.architecture.realarchitecture.domain.eventbus.EventResponse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by liushuo on 16/3/27.
 */
public class RequestController implements RequestControllable {
    private Set<String> mRequestIds = new HashSet<>();

    public RequestController() {
    }

    @Override
    public void onPreNetRequest(EventPreNetRequest event) {
        //do nothing
    }

    @Override
    public void onNetRequestError(EventNetError error) {
        removeRequestId(error.mRequest.getRequestId());
    }

    @Override
    public void onRequestCanceled(EventRequestCanceled cancel) {
        removeRequestId(cancel.mRequest.getRequestId());
    }

    private void removeRequestId(String requestId) {
        if (!TextUtils.isEmpty(requestId)) {
            mRequestIds.remove(requestId);
        }
    }

    @Override
    public void enqueueRequest(Request request) {

        //首先执行同步操作
        String requestId = request.getRequestId();
        if (!TextUtils.isEmpty(requestId)) {
            mRequestIds.add(request.getRequestId());
        }

        request.attachRequestController(this);

        //最后执行将任务添加到任务队列
        RequestManager.getInstance().enqueueRequest(request);
    }

    @Override
    public void cancelRequest() {
        Iterator<String> itr = mRequestIds.iterator();
        while (itr.hasNext()) {
            String requestId = itr.next();
            RequestManager.getInstance().cancelRequest(requestId);
            itr.remove();
        }
    }

    @Override
    public void onReceiveResponse(EventResponse event) {
        if (event.mRequest.isDone()) {
            removeRequestId(event.mRequest.getRequestId());
        }
    }

    @Override
    public boolean isManagedRequest(Request request) {
        if (request == null) return false;

        return request.getRequestController() == this;
    }
}
