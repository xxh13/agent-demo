package com.alibaba.dubbo.performance.demo.agent.netty;

import com.alibaba.dubbo.performance.demo.agent.client.ConsumerClient;
import com.alibaba.dubbo.performance.demo.agent.proto.Response;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;


/**
 * 接收来自consumer的消息
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
    private ConsumerClient consumerClient = new ConsumerClient();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        if (fullHttpRequest.method() == HttpMethod.POST) {
            consumerClient.invoke(fullHttpRequest, e->handleResponse(e, fullHttpRequest, channelHandlerContext));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("receive connection from consumer");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("close connection from consumer");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
    }

    private void handleResponse(Object object, FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        if (object != null) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.content().writeBytes(Unpooled.copiedBuffer(String.valueOf(((Response.SearchResponse) object).getHash()), CharsetUtil.UTF_8));

            boolean keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);
            if (!keepAlive) {
                ctx.executor().submit(() -> {
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                });
            } else {
                HttpUtil.setKeepAlive(response, true);
                HttpUtil.setContentLength(response, response.content().readableBytes());
                ctx.executor().submit(() -> {
                    ctx.writeAndFlush(response);
                });
            }
        } else {
            ctx.executor().submit(
                () -> {
                    ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                }
            );
        }

    }
}
