package com.architecture.realarchitecture.presention.base;

import android.os.Bundle;

import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.domain.FreedomRequestHandler;
import com.architecture.realarchitecture.domain.request.Request;
import com.architecture.realarchitecture.domain.RequestRespondable;
import com.architecture.realarchitecture.domain.eventbus.EventRequestCanceled;
import com.architecture.realarchitecture.domain.eventbus.EventNetError;
import com.architecture.realarchitecture.domain.eventbus.EventPreNetRequest;
import com.architecture.realarchitecture.domain.eventbus.EventResponse;
import com.architecture.realarchitecture.domain.request.controller.RequestControllable;
import com.architecture.realarchitecture.domain.request.controller.RequestController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Author: xumin
 * Date: 2016/4/7
 */
public abstract class RequestControllableActivity extends BaseActivity implements RequestControllable {

    private RequestController mController;
    private FreedomRequestHandler mFreedomRequestHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mController = new RequestController();
        mFreedomRequestHandler = new FreedomRequestHandler();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onPreNetRequest(EventPreNetRequest event) {
        if (mFreedomRequestHandler.isFreedomRequest(event.mRequest)) {
            mFreedomRequestHandler.onPreNetRequest(event);
            return;
        }

        if (!isManagedRequest(event.mRequest)) return;

        mController.onPreNetRequest(event);
        handlePreNetRequest(event.mRequest);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onNetRequestError(EventNetError error) {
        if (mFreedomRequestHandler.isFreedomRequest(error.mRequest)) {
            mFreedomRequestHandler.onNetRequestError(error);
            return;
        }
        if (!isManagedRequest(error.mRequest)) return;

        mController.onNetRequestError(error);
        handleNetRequestError(error.mRequest, error.mRB);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onRequestCanceled(EventRequestCanceled cancel) {
        if (mFreedomRequestHandler.isFreedomRequest(cancel.mRequest)) {
            mFreedomRequestHandler.onRequestCanceled(cancel);
            return;
        }

        if (!isManagedRequest(cancel.mRequest)) return;

        mController.onRequestCanceled(cancel);
        handleRequestCanceled(cancel.mRequest);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onReceiveResponse(EventResponse event) {
        if (mFreedomRequestHandler.isFreedomRequest(event.mRequest)) {
            mFreedomRequestHandler.onReceiveResponse(event);
            return;
        }

        if (!isManagedRequest(event.mRequest)) return;

        mController.onReceiveResponse(event);
        handleReceivedResponse(event);
    }


    @Override
    public void enqueueRequest(Request request) {
        mController.enqueueRequest(request);
    }

    @Override
    public void cancelRequest() {
        mController.cancelRequest();
    }

    @Override
    public boolean isManagedRequest(Request request) {
        return mController.isManagedRequest(request);
    }

    public void registerFreedomRequestHandler(String requestId, RequestRespondable handler) {
        mFreedomRequestHandler.registerFreedomRequestHandler(requestId, handler);
    }


    protected abstract void handlePreNetRequest(Request request);

    protected abstract void handleNetRequestError(Request request, ResponseHeader rb);

    //多数情况下 用户可能不关心请求是否被取消，有特殊需求的页面，可以重写该方法
    protected void handleRequestCanceled(Request request) {
    }

    protected abstract void handleReceivedResponse(EventResponse event);
}
