/**
 * Copyright (c) 2014 Guanghe.tv
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/
package com.architecture.realarchitecture.domain.request;

import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.datasource.DALFactory;
import com.architecture.realarchitecture.datasource.net.ResponseSchema;
import com.architecture.realarchitecture.datasource.net.RetrofitClient;
import com.architecture.realarchitecture.domain.CacheDispatcher;
import com.architecture.realarchitecture.domain.DataFrom;
import com.architecture.realarchitecture.domain.DeDaoService;
import com.architecture.realarchitecture.domain.Request;
import com.architecture.realarchitecture.domain.eventbus.EventNetError;
import com.architecture.realarchitecture.domain.eventbus.EventPreNetRequest;
import com.architecture.realarchitecture.domain.eventbus.EventResponse;
import com.architecture.realarchitecture.domain.strategy.base.ObjectGetStrategy;
import com.architecture.realarchitecture.domain.strategy.object.ForceNetForObject;
import com.architecture.realarchitecture.domain.strategy.object.Level3CacheForObject;
import com.architecture.realarchitecture.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by liuzhi on 2016/3/24.
 * -------------------------------
 */
public class DemoObjectRequest extends Request<Map<String, Object>> {

    private ObjectGetStrategy mRequestStragety;
    private String mId;

    public DemoObjectRequest(String dataType) {
        super(dataType);
        mId = "9";
        mRequestId = "demo_object_request";
    }

    @Override
    public Call<Map<String, Object>> getCall() {
        return RetrofitClient.getApiService(DeDaoService.class).testGetObject();
    }

    @Override
    protected Map<String, Object> adaptStructForCache(Map<String, Object> data) {

        data = (Map<String, Object>) data.get("data");
        if (data == null) data = new HashMap<>();

        LogUtils.d("请求:");
        LogUtils.d(data);

        return data;
    }

    /**
     * @param data data的数据结构为transformForUiLayer返回的数据结构
     */
    @Override
    protected void cacheNetResponse(Map<String, Object> data) {
        //1.检查是否需要缓存内存
        DALFactory.getMemoryStorage().cacheObjectDataInMemory(mDataType, String.valueOf(data.get("id")), data);
        //2.数据缓存本地
        CacheDispatcher.getInstance().dispatchDataCache(mDataType, data);

        LogUtils.d("请求:");
        LogUtils.d(data);

    }

    @Override
    public void perform() {
        super.perform();

        if (isResponseValid()) {
            mRequestStragety = new Level3CacheForObject(mDataType, mId, getCall(), this);
        } else {
            mRequestStragety = new ForceNetForObject(mDataType, mId, getCall(), this);
        }
        mRequestStragety.fetchData();
    }


    @Override
    public String toString() {
        return this.getClass().toString() + ",isDone:" + isDone();
    }
}
