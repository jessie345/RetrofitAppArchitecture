package com.architecture.realarchitecture.domain;

import android.text.TextUtils;

import com.architecture.realarchitecture.domain.eventbus.EventRequestCanceled;
import com.architecture.realarchitecture.domain.eventbus.EventNetError;
import com.architecture.realarchitecture.domain.eventbus.EventPreNetRequest;
import com.architecture.realarchitecture.domain.eventbus.EventResponse;
import com.architecture.realarchitecture.domain.request.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liushuo on 16/4/13.
 * Freedom Request is that has no RequestController control it
 */
public class FreedomRequestHandler implements RequestRespondable {

    private Map<String, RequestRespondable> mFreedomRequestHandler = new HashMap<>();

    public boolean isFreedomRequest(Request request) {
        return request.getRequestController() == null;
    }


    public void registerFreedomRequestHandler(String requestId, RequestRespondable handler) {
        if (TextUtils.isEmpty(requestId) || handler == null) return;

        mFreedomRequestHandler.put(requestId, handler);
    }

    private RequestRespondable getFreedomRequestHandler(Request request) {
        if (request == null) return null;

        String requestId = request.getRequestId();
        if (TextUtils.isEmpty(requestId))
            return null;

        RequestRespondable handler = mFreedomRequestHandler.get(requestId);
        return handler;
    }


    @Override
    public void onPreNetRequest(EventPreNetRequest event) {
        RequestRespondable handler = getFreedomRequestHandler(event.mRequest);
        if (handler == null) return;

        handler.onPreNetRequest(event);
    }

    @Override
    public void onNetRequestError(EventNetError error) {
        RequestRespondable handler = getFreedomRequestHandler(error.mRequest);
        if (handler == null) return;

        handler.onNetRequestError(error);
    }

    @Override
    public void onRequestCanceled(EventRequestCanceled cancel) {
        RequestRespondable handler = getFreedomRequestHandler(cancel.mRequest);
        if (handler == null) return;

        handler.onRequestCanceled(cancel);
    }

    @Override
    public void onReceiveResponse(EventResponse event) {
        RequestRespondable handler = getFreedomRequestHandler(event.mRequest);
        if (handler == null) return;

        handler.onReceiveResponse(event);
    }
}
