package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import java.util.concurrent.ConcurrentHashMap;

public class ConsumerRequestHolder {

    private static ConcurrentHashMap<Long, ResponseFuture> processingRpc = new ConcurrentHashMap<>(1024);

    public static void put(long requestId, ResponseFuture responseFuture){
        processingRpc.put(requestId, responseFuture);
    }

    public static ResponseFuture get(long requestId){
        return processingRpc.get(requestId);
    }

    public static void remove(long requestId){
        processingRpc.remove(requestId);
    }
}
