package com.architecture.realarchitecture.domain.eventbus;

import com.architecture.realarchitecture.datasource.net.ResponseHeader;
import com.architecture.realarchitecture.domain.Request;

/**
 * Created by liushuo on 16/3/20.
 */
public class EventRequestCanceled {
    public Request mRequest;

    public EventRequestCanceled(Request request) {
        this.mRequest = request;
    }

}
