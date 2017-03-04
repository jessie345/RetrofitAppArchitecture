package com.architecture.realarchitecture.domain.eventbus;

import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.domain.request.Request;

/**
 * Created by liushuo on 16/3/20.
 */
public class EventNetError {
    public ResponseHeader mRB;
    public Request mRequest;

    public EventNetError(Request request, ResponseHeader rb) {
        this.mRB = rb;
        this.mRequest = request;
    }

}
