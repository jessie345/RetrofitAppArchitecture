package com.architecture.realarchitecture.domain;

import android.support.annotation.CallSuper;
import android.text.TextUtils;

import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.domain.eventbus.EventNetError;
import com.architecture.realarchitecture.domain.eventbus.EventPreNetRequest;
import com.architecture.realarchitecture.domain.eventbus.EventResponse;
import com.architecture.realarchitecture.domain.request.controller.RequestControllable;
import com.architecture.realarchitecture.manager.PreferenceManager;
import com.architecture.realarchitecture.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import retrofit2.Call;

/**
 * Created by liushuo on 16/3/17.
 * 注意:请求网络数据，底层可能返回null，所以调用者需要进行判空处理
 */
public abstract class Request<K> implements ResponseListener<K> {
    private static final int RESPONSE_VALID_THRESHOLD = 30 * 60 * 1000;

    public enum State {IDLE, RUNNING, DONE}

    protected String mRequestTag;//客户端可以根据tag取消请求的执行
    protected String mDataType;
    protected String mRequestId;//可以用于判定响应是否过期
    private RequestControllable mRequestController;

    private volatile K mResult;
    private volatile State mState = State.IDLE;


    public Request(String docType) {
        this.mDataType = docType;
        mRequestTag = getClass().getSimpleName();
    }

    public abstract Call<Map<String, Object>> getCall();

    /**
     * 子类必须调用父类方法，初始化请求状态
     */
    @CallSuper
    public void perform() {
        if (mState != State.IDLE) throw new IllegalStateException("请求无法重复添加");

        mState = State.RUNNING;
    }

    protected abstract K transformForUiLayer(Map<String, Object> v);

    protected abstract void cacheNetResponse(K data);


    public K getResult() {
        return mResult;
    }

    protected void setDone(boolean isDone) {
        mState = isDone ? State.DONE : State.RUNNING;
    }

    public boolean isRunning() {
        return mState == State.RUNNING;
    }

    public boolean isDone() {
        return mState == State.DONE;
    }


    /**
     * 判定请求过期的策略由父亲类决定，子类提供requestId标示不同的请求，为空则每次都过期
     *
     * @return
     */
    public final boolean isResponseValid() {
        if (TextUtils.isEmpty(mRequestId)) return false;

        long lastRequestTime = PreferenceManager.getLongValue(mRequestId);
        long currentTime = System.currentTimeMillis();
        return currentTime - lastRequestTime < getResponseValidThreshold();
    }

    /**
     * 子类如果有不同的过期时间，需要重写
     *
     * @return
     */
    protected int getResponseValidThreshold() {
        return RESPONSE_VALID_THRESHOLD;
    }

    public void setRequestTag(String requestTag) {
        this.mRequestTag = requestTag;
    }

    public String getRequestTag() {
        return mRequestTag;
    }

    public void attachRequestController(RequestControllable requestController) {
        this.mRequestController = requestController;
    }

    public RequestControllable getRequestController() {
        return mRequestController;
    }

    @CallSuper
    @Override
    public void onRetrofitResponse(ResponseHeader rb, Map<String, Object> header, Map<String, Object> content) {
        if (content == null) return;

        extendNetResponseValid();
        mResult = transformForUiLayer(content);
        cacheNetResponse(mResult);
        setDone(true);

        //通知ui网络返回
        dispatchRetrofitResponse(header);
        LogUtils.d("net数据返回成功：" + mDataType);

    }

    @CallSuper
    @Override
    public void onCacheResponse(K k, boolean isDone) {
        mResult = k;
        setDone(isDone);

        //通知ui缓存数据返回
        EventBus.getDefault().post(new EventResponse(this, DataFrom.CACHE));
        LogUtils.d("cache数据返回成功：" + mDataType);
    }

    @Override
    public void preNetRequest() {
        EventBus.getDefault().post(new EventPreNetRequest(this));
        LogUtils.d("即将执行网络请求：" + mDataType);
    }

    @Override
    public void onNetRequestError(ResponseHeader httpResponse) {
        EventBus.getDefault().post(new EventNetError(this, httpResponse));

    }

    protected void dispatchRetrofitResponse(Map<String, Object> header) {
        if (header == null) {
            EventBus.getDefault().post(new EventNetError(this, ResponseHeader.create(-1, "header is null")));
            return;
        }

        int code = Integer.parseInt((String) header.get("c"));
        if (code == 0) {
            EventBus.getDefault().post(new EventResponse(this, DataFrom.NET));
        } else {
            String message = (String) header.get("e");
            EventBus.getDefault().post(new EventNetError(this, ResponseHeader.create(code, message)));
        }
    }

    private void extendNetResponseValid() {
        if (!TextUtils.isEmpty(mRequestId)) {
            PreferenceManager.putLong(mRequestId, System.currentTimeMillis());
        }
    }

}
