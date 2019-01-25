package com.alibaba.dubbo.performance.demo.agent.loop;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class RpcFutureLoop {

    private Map<RpcFuture, Consumer<Object>> mapping;
    private static RpcFutureLoop rpcFutureLoop = new RpcFutureLoop();

    private RpcFutureLoop() {
        mapping = new ConcurrentHashMap<>(1024);
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                long l = System.nanoTime();
                for (Map.Entry<RpcFuture, Consumer<Object>> entry : mapping.entrySet()) {
                    if (entry.getKey().isDone()) {
                        Object r = null;
                        try {
                            r = entry.getKey().get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        entry.getValue().accept(r);
                        mapping.remove(entry.getKey());
                    }
                }
                if (System.nanoTime() - l < 100) {
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static RpcFutureLoop getRpcFutureLoop() {
        return rpcFutureLoop;
    }

    public void submit(RpcFuture rpcFuture, Consumer<Object> consumer) {
        mapping.put(rpcFuture, consumer);
    }
}
