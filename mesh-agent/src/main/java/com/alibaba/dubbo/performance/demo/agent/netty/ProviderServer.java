package com.alibaba.dubbo.performance.demo.agent.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProviderServer {

    private Logger logger = LoggerFactory.getLogger(ProviderServer.class);

    private ServerBootstrap bootstrap;

    private Channel channel;

    public ProviderServer() {
        this.bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup())
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ProviderInitializer())
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);

    }

    public void start(int port) {
        try {
            channel = bootstrap.bind(port).sync().channel().closeFuture().sync().channel();
            logger.info("mesh-agent-v2.0 netty server started at port:" + port);
        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }

}
