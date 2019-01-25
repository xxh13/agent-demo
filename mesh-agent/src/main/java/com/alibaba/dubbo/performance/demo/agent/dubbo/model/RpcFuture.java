package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

public class RpcFuture {

    private RpcResponse response;

    private volatile boolean isDone = false;

    public boolean isDone() {
        return isDone;
    }

    public Object get() throws InterruptedException {
        if (null != response)
            return response.getBytes();
        return null;
    }

    public void done(RpcResponse response){
        this.isDone = true;
        this.response = response;
    }
}
