package com.alibaba.dubbo.performance.demo.agent.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicePool {
    public static ExecutorService multiThreadServicePool = Executors.newFixedThreadPool(4);
    public static ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
}
