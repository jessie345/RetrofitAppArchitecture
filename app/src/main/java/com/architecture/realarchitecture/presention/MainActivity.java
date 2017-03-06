package com.architecture.realarchitecture.presention;

import android.os.Bundle;
import android.util.Log;

import com.architecture.realarchitecture.R;
import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.domain.DataFrom;
import com.architecture.realarchitecture.domain.request.Request;
import com.architecture.realarchitecture.domain.eventbus.EventResponse;
import com.architecture.realarchitecture.presention.request.DemoListRequest;
import com.architecture.realarchitecture.presention.request.DemoObjectRequest;
import com.architecture.realarchitecture.presention.base.RequestControllableActivity;
import com.architecture.realarchitecture.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends RequestControllableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Request<Map<String, Object>> request = new DemoObjectRequest("testObject");
        Request<List<Map<String, Object>>> request = new DemoListRequest("testArray");
        enqueueRequest(request);

    }

    @Override
    protected void handlePreNetRequest(Request request) {
        //showloading
    }

    @Override
    protected void handleNetRequestError(Request request, ResponseHeader rb) {
        //toast
    }

    @Override
    protected void handleRequestCanceled(Request request) {
        // tick request canceled
    }

    @Override
    protected void handleReceivedResponse(EventResponse event) {
        if (event.mRequest instanceof DemoObjectRequest) {
            Map<String, Object> data = ((DemoObjectRequest) event.mRequest).getResult();
            DataFrom from = event.mDataFrom;

            Log.d("retrofit", String.format("from:%s,content:%s", from.toString(), data.toString()));
        }
    }
}
