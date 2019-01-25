package com.alibaba.dubbo.performance.demo.agent.netty;

import com.alibaba.dubbo.performance.demo.agent.client.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.proto.Request;
import com.alibaba.dubbo.performance.demo.agent.proto.Response;
import com.alibaba.dubbo.performance.demo.agent.service.ServicePool;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class ProviderHandler extends ChannelInboundHandlerAdapter {

    private RpcClient client = new RpcClient();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        Request.SearchRequest request = (Request.SearchRequest) msg;

        /**
         * step1 we read from consumer agent, then we will pass to provider
         */

        ServicePool.singleThreadPool.submit(() -> {
            try {
                client.invoke(request.getInterfaceName(), request.getMethod(), request.getParameterTypesString(),
                        request.getParameter(), e -> handleResponse(e, request, ctx));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    private void handleResponse(Object object, Request.SearchRequest request, ChannelHandlerContext ctx) {
        Response.SearchResponse.Builder builder = Response.SearchResponse.newBuilder();
        builder.setResponseId(request.getRequestId());
        try {
            if (null != object) {
                byte[] result = (byte[]) object;
                builder.setHash(JSONObject.parseObject(result, Integer.class));
                ctx.executor().submit(
                    () -> {
                        ctx.writeAndFlush(builder.build());
                    }
                );
            } else {
                builder.setHash(-1);
                ctx.writeAndFlush(builder);
            }
        } catch (Exception e) {
            builder.setHash(-1);
            ctx.executor().submit(
                () -> {
                    ctx.writeAndFlush(builder.build());
                }
            );
            e.printStackTrace();
        }
    }
}
