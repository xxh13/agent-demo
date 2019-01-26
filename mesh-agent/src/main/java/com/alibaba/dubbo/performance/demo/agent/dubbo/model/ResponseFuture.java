package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import com.alibaba.dubbo.performance.demo.agent.proto.Response;

import java.util.function.Consumer;

public class ResponseFuture {
    private Response.SearchResponse searchResponse;
    private volatile boolean isDone = false;
    private Consumer<Object> callback;

    public boolean isDone() {
        return isDone;
    }

    public Object get() {
        if (searchResponse == null)
            return null;
        return searchResponse;
    }

    public void setCallback(Consumer<Object> callback) {
    	this.callback = callback;
    }

    public void done(Response.SearchResponse searchResponse) {
        this.isDone = true;
        this.searchResponse = searchResponse;
        this.callback.accept(this.searchResponse);
    }


}
