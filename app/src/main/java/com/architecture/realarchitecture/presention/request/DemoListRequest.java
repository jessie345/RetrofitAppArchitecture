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
package com.architecture.realarchitecture.presention.request;

import com.architecture.realarchitecture.datasource.DALFactory;
import com.architecture.realarchitecture.datasource.net.RetrofitClient;
import com.architecture.realarchitecture.domain.CacheDispatcher;
import com.architecture.realarchitecture.presention.apiservice.DeDaoService;
import com.architecture.realarchitecture.domain.request.Request;
import com.architecture.realarchitecture.domain.strategy.array.ForceNetForArray;
import com.architecture.realarchitecture.domain.strategy.array.Level3CacheForArray;
import com.architecture.realarchitecture.domain.strategy.base.ArrayGetStrategy;
import com.architecture.realarchitecture.utils.LogUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by liuzhi on 2016/3/24.
 * -------------------------------
 */
public class DemoListRequest extends Request<List<Map<String, Object>>> {

    private ArrayGetStrategy mRequestStragety;

    public DemoListRequest(String dataType) {
        super(dataType);

        setRequestId("demo_array_request");
    }

    @Override
    public Call<Map<String, Object>> getCall() {
        return DALFactory.getApiService(DeDaoService.class).testGetList();
    }

    @Override
    protected List<Map<String, Object>> adaptStructForCache(Map<String, Object> data) {

        List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("structure");
        if (list == null) list = new ArrayList<>();

        LogUtils.d("请求:");
        LogUtils.d(list);

        return list;
    }

    /**
     * @param data data的数据结构为transformForUiLayer返回的数据结构
     */
    @Override
    protected void cacheNetResponse(List<Map<String, Object>> data) {
        //1.检查是否需要缓存内存
        DALFactory.getMemoryStorage().cacheArrayDatasInMemory(mDataType, data);
        //2.数据缓存本地
        CacheDispatcher.getInstance().dispatchDataCache(mDataType, data.toArray(new HashMap[0]));

        LogUtils.d("请求:");
        LogUtils.d(data);

    }

    @Override
    public TypeReference getTypeReference() {
        return null;
    }

    @Override
    public void perform() {
        super.perform();

        if (isResponseValid()) {
            mRequestStragety = new Level3CacheForArray(mDataType, getCall(), this);
        } else {
            mRequestStragety = new ForceNetForArray(mDataType, getCall(), this);
        }
        mRequestStragety.fetchData();
    }


    @Override
    public String toString() {
        return this.getClass().toString() + ",isDone:" + isDone();
    }
}
