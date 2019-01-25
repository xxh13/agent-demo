package com.alibaba.dubbo.performance.demo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.ConnectManager;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.JsonUtils;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Request;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcInvocation;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcRequestHolder;

import com.alibaba.dubbo.performance.demo.agent.loop.RpcFutureLoop;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.function.Consumer;

public class RpcClient {
    private Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private ConnectManager connectManager;

    public RpcClient(IRegistry registry){
        this.connectManager = ConnectManager.getConnectManager();
    }

    public RpcClient() {
        this.connectManager = ConnectManager.getConnectManager();
    }

    private RpcFutureLoop loop = RpcFutureLoop.getRpcFutureLoop();

    public void invoke(String interfaceName, String method, String parameterTypesString,
                         String parameter, Consumer<Object> consumer) throws Exception {

        Channel channel = connectManager.getChannel();

        RpcInvocation invocation = new RpcInvocation();
        invocation.setMethodName(method);
        invocation.setAttachment("path", interfaceName);
        invocation.setParameterTypes(parameterTypesString);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        JsonUtils.writeObject(parameter, writer);
        invocation.setArguments(out.toByteArray());

        Request request = new Request();
        request.setVersion("2.0.0");
        request.setTwoWay(true);
        request.setData(invocation);

//        logger.info("requestId=" + request.getId());

        RpcFuture future = new RpcFuture();
        RpcRequestHolder.put(String.valueOf(request.getId()),future);

        channel.eventLoop().submit(
                () -> {
                    channel.writeAndFlush(request);
                }
        );

        RpcRequestHolder.put(String.valueOf(request.getId()), future);
        loop.submit(future, consumer);
    }
}
