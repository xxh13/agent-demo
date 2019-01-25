package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import com.alibaba.dubbo.performance.demo.agent.proto.Response;

public class ResponseFuture {
    private Response.SearchResponse searchResponse;
    private volatile boolean isDone = false;

    public boolean isDone() {
        return isDone;
    }

    public Object get() {
        if (searchResponse == null)
            return null;
        return searchResponse;
    }

    public void done(Response.SearchResponse searchResponse) {
        this.isDone = true;
        this.searchResponse = searchResponse;
    }
}
