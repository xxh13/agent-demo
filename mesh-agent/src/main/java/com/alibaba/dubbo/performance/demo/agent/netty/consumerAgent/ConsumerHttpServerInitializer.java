package com.alibaba.dubbo.performance.demo.agent.netty.consumerAgent;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class ConsumerHttpServerInitializer extends ChannelInitializer{

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(new HttpRequestDecoder());
        channel.pipeline().addLast(new HttpObjectAggregator(65535));
        channel.pipeline().addLast(new HttpResponseEncoder());
        channel.pipeline().addLast(new HttpServerHandler());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
