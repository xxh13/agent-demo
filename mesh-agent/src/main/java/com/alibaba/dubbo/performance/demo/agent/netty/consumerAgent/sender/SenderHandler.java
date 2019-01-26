package com.alibaba.dubbo.performance.demo.agent.netty.consumerAgent.sender;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ConsumerRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.ResponseFuture;
import com.alibaba.dubbo.performance.demo.agent.proto.Response;
import com.alibaba.dubbo.performance.demo.agent.service.ServicePool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * 接收来自provider agent的消息
 */
public class SenderHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Response.SearchResponse response = (Response.SearchResponse) msg;
        ServicePool.singleThreadPool.submit(
            () -> {
                long requestId = response.getResponseId();
                ResponseFuture future = ConsumerRequestHolder.get(requestId);
                if(null != future) {
                    ConsumerRequestHolder.remove(requestId);
                    future.done(response);
                }
            }
        );
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
