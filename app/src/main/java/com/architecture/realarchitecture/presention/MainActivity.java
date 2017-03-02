package com.architecture.realarchitecture.presention;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.architecture.realarchitecture.R;
import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.domain.DataFrom;
import com.architecture.realarchitecture.domain.Request;
import com.architecture.realarchitecture.domain.eventbus.EventResponse;
import com.architecture.realarchitecture.domain.request.DemoRequest;
import com.architecture.realarchitecture.presention.base.RequestControllableActivity;

import java.util.Map;

public class MainActivity extends RequestControllableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Request<Map<String, Object>> request = new DemoRequest("doty", "ii");
        enqueueRequest(request);
    }

    @Override
    protected void handlePreNetRequest(Request request) {
//showloading
    }

    @Override
    protected void handleErrorWhenRequest(Request request, ResponseHeader rb) {
//toast
    }

    @Override
    protected void handleReceivedResponse(EventResponse event) {
      if(event.mRequest instanceof DemoRequest){
          Map<String,Object> data=((DemoRequest) event.mRequest).getResult();
          DataFrom from=event.mDataFrom;
      }
    }
}
