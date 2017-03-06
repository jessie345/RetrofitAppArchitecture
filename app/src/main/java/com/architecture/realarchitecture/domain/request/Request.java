package com.architecture.realarchitecture.domain.request;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.architecture.realarchitecture.datasource.net.HeaderSchema;
import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.datasource.net.StatusCode;
import com.architecture.realarchitecture.domain.DataFrom;
import com.architecture.realarchitecture.domain.ResponseListener;
import com.architecture.realarchitecture.domain.eventbus.EventRequestCanceled;
import com.architecture.realarchitecture.domain.eventbus.EventNetError;
import com.architecture.realarchitecture.domain.eventbus.EventPreNetRequest;
import com.architecture.realarchitecture.domain.eventbus.EventResponse;
import com.architecture.realarchitecture.domain.request.controller.RequestControllable;
import com.architecture.realarchitecture.manager.PreferenceManager;
import com.architecture.realarchitecture.utils.LogUtils;
import com.architecture.realarchitecture.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import retrofit2.Call;

/**
 * Created by liushuo on 16/3/17.
 */
public abstract class Request<K> implements ResponseListener<K> {
    private static final int RESPONSE_VALID_THRESHOLD = 30 * 60 * 1000;

    public enum State {IDLE, RUNNING, DONE}

    protected String mDataType;
    /*请求的唯一标识,对等于get 请求中的请求路径，post请求中的路径+参数*/
    private String mRequestId;

    protected boolean isCanceled;
    private RequestControllable mRequestController;

    private volatile K mResult;
    private volatile State mState = State.IDLE;


    public Request(String docType) {
        this.mDataType = docType;

        mRequestId = getClass().getSimpleName();
    }

    @NonNull
    public abstract Call<Map<String, Object>> getCall();

    /**
     * 子类必须调用父类方法，初始化请求状态
     */
    @CallSuper
    public void perform() {
        if (mState != State.IDLE) throw new IllegalStateException("请求无法重复添加");

        //初始化请求的默认状态
        setCanceled(false);

        mState = State.RUNNING;
    }

    /**
     * 将服务器返回的数据格式 转换适配到可以进行缓存的数据结构
     *
     * @param v
     * @return
     */
    protected abstract K adaptStructForCache(Map<String, Object> v);

    protected abstract void cacheNetResponse(K data);


    public K getResult() {
        return mResult;
    }

    /**
     * 返回类型引用，用于jackson 将map转换成bean
     * eg.TypeReference ref = new TypeReference<List<User>>() { };
     * 将map list格式化成List<User>
     * <p>
     * TypeReference ref = new TypeReference<User>() { };
     * 将map 格式化成User
     *
     * @return
     */
    public abstract TypeReference getTypeReference();

    public <V> V getResultBean() {
        if (getResult() == null) return null;

        TypeReference typeRef = getTypeReference();
        if (typeRef == null) return null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            return (V) mapper.convertValue(getResult(), typeRef);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
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

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    /**
     * requestId的默认值为类名
     *
     * @return
     */
    public String getRequestId() {
        return mRequestId;
    }

    public void setRequestId(@NonNull String mRequestId) {
        if (TextUtils.isEmpty(mRequestId))
            throw new IllegalArgumentException("不能将请求id设置为空值,有任何疑问，联系作者");

        this.mRequestId = mRequestId;
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
        boolean valid = currentTime - lastRequestTime < getResponseValidThreshold();

        LogUtils.d("请求:");
        LogUtils.d("响应有效:" + valid);

        return valid;
    }

    /**
     * 子类如果有不同的过期时间，需要重写
     *
     * @return
     */
    protected int getResponseValidThreshold() {
        return RESPONSE_VALID_THRESHOLD;
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

        extendNetResponseValid();
        mResult = adaptStructForCache(content);
        cacheNetResponse(mResult);
        setDone(true);

        //通知ui网络返回
        dispatchRetrofitResponse(header);

        LogUtils.d("请求:");
        LogUtils.d(mResult);


    }

    @CallSuper
    @Override
    public void onCacheResponse(K k, boolean isDone) {
        if (isCanceled()) {
            EventBus.getDefault().post(new EventRequestCanceled(this));
            LogUtils.d("请求被取消：" + mDataType);

            return;
        }


        mResult = k;
        setDone(isDone);

        //通知ui缓存数据返回
        EventBus.getDefault().post(new EventResponse(this, DataFrom.CACHE));

        LogUtils.d("请求:");
        LogUtils.d(mResult);

    }

    @Override
    public void preNetRequest() {
        EventBus.getDefault().post(new EventPreNetRequest(this));
        LogUtils.d("即将执行网络请求：" + mDataType);
    }

    @Override
    public void onNetRequestError(ResponseHeader httpResponse) {
        EventBus.getDefault().post(new EventNetError(this, httpResponse));

        LogUtils.d("网络发生错误：" + mDataType);
    }

    @Override
    public void onNetCanceled(ResponseHeader httpResponse) {
        EventBus.getDefault().post(new EventRequestCanceled(this));

        LogUtils.d("请求被取消：" + mDataType);
    }

    protected void dispatchRetrofitResponse(Map<String, Object> header) {
        if (header == null) {
            EventBus.getDefault().post(new EventNetError(this, ResponseHeader.create(-1, "header is null")));
            return;
        }

        int code = (int) header.get(HeaderSchema.code);
        if (code == StatusCode.Server.SERVER_SUCCESS) {
            EventBus.getDefault().post(new EventResponse(this, DataFrom.NET));
        } else {
            String message = (String) header.get(HeaderSchema.error_msg);
            EventBus.getDefault().post(new EventNetError(this, ResponseHeader.create(code, message)));
        }
    }

    private void extendNetResponseValid() {
        if (!TextUtils.isEmpty(mRequestId)) {
            PreferenceManager.putLong(mRequestId, System.currentTimeMillis());

            LogUtils.d("请求:");
            LogUtils.d("延长响应有效时间");

        }
    }

}
