package com.alibaba.dubbo.performance.demo.agent.loop;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ResponseFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ConsumerClientLoop {
    private ConcurrentHashMap<ResponseFuture, Consumer<Object>> map;
    private static ConsumerClientLoop consumerClientLoop = new ConsumerClientLoop();

    private ConsumerClientLoop() {
        map = new ConcurrentHashMap<>(1024);

        Executors.newSingleThreadExecutor().submit(
            () -> {
                while (true) {
                    long l = System.nanoTime();
                    for (Map.Entry<ResponseFuture, Consumer<Object>> entry : map.entrySet()) {
                        if (entry.getKey().isDone()) {
                            Object r = null;
                            try {
                                r = entry.getKey().get();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            entry.getValue().accept(r);
                            map.remove(entry.getKey());
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
            }
        );
    }

    public static ConsumerClientLoop getConsumerClientLoop() {
        return consumerClientLoop;
    }

    public void submit(ResponseFuture responseFuture, Consumer<Object> consumer) {
        map.put(responseFuture, consumer);
    }
}
