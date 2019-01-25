package com.alibaba.dubbo.performance.demo.agent.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ConsumerRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ResponseFuture;
import com.alibaba.dubbo.performance.demo.agent.loop.ConsumerClientLoop;
import com.alibaba.dubbo.performance.demo.agent.netty.ConsumerServer;
import com.alibaba.dubbo.performance.demo.agent.service.ServicePool;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;


import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class ConsumerClient {
    private static AtomicLong requestId = new AtomicLong(0);

    private static ConsumerServer consumerServer = new ConsumerServer();

    private ConsumerClientLoop consumerClientLoop = ConsumerClientLoop.getConsumerClientLoop();

    public void invoke(FullHttpRequest fullHttpRequest, Consumer<Object> consumer) throws Exception
    {
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), fullHttpRequest);

        ServicePool.singleThreadPool.submit(
            () -> {
                try {
//                    InterfaceHttpData interfaceData = decoder.getBodyHttpData("interface");
//                    InterfaceHttpData methodData = decoder.getBodyHttpData("method");
//                    InterfaceHttpData parameterTypesStringData = decoder.getBodyHttpData("parameterTypesString");
                    InterfaceHttpData parameterData = decoder.getBodyHttpData("parameter");
//                    String interfaceName = "";
//                    if (interfaceData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
//                        Attribute attribute = (Attribute) interfaceData;
//                        interfaceName = attribute.getValue();
//                    }
//                    String method = "";
//                    if (methodData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
//                        Attribute attribute = (Attribute) methodData;
//                        method = attribute.getValue();
//                    }
//                    String parameterTypesString = "";
//                    if (parameterTypesStringData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
//                        Attribute attribute = (Attribute) parameterTypesStringData;
//                        parameterTypesString = attribute.getValue();
//                    }
                    String parameterString = "";
                    if (parameterData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                        Attribute attribute = (Attribute) parameterData;
                        parameterString = attribute.getValue();
                    }
                    com.alibaba.dubbo.performance.demo.agent.proto.Request.SearchRequest.Builder builder =
                            com.alibaba.dubbo.performance.demo.agent.proto.Request.SearchRequest.newBuilder();

                    builder.setRequestId(requestId.getAndIncrement());
                    builder.setInterfaceName("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                    builder.setMethod("hash");
                    builder.setParameterTypesString("Ljava/lang/String;");
                    builder.setParameter(parameterString);

                    ResponseFuture future = new ResponseFuture();
                    ConsumerRequestHolder.put(builder.getRequestId(), future);

                    Channel channel = consumerServer.getChannel();

                    channel.eventLoop().submit(
                        () -> {
                            channel.writeAndFlush(builder.build());
                        }
                    );

                    consumerClientLoop.submit(future, consumer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );

    }
}
