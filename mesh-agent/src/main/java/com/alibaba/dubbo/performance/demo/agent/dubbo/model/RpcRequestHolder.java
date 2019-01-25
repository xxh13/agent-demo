package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import java.util.concurrent.ConcurrentHashMap;

public class RpcRequestHolder {

    // key: requestId     value: RpcFuture
    public static ConcurrentHashMap<String,RpcFuture> processingRpc = new ConcurrentHashMap<>(512);

    public static void put(String requestId,RpcFuture rpcFuture){
        processingRpc.put(requestId,rpcFuture);
    }

    public static RpcFuture get(String requestId){
        return processingRpc.get(requestId);
    }

    public static void remove(String requestId){
        processingRpc.remove(requestId);
    }
}
