package com.alibaba.dubbo.performance.demo.agent.netty;

import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class ConsumerServer {

    private volatile Bootstrap bootstrap;

    private Channel[] channels;
    private AtomicInteger atomicLong = new AtomicInteger(0);


    public ConsumerServer() {
        initBootstrap();
        try {
            int channelSize = 0, chanelIndex = 0;
            Map<Endpoint, Integer> map = new EtcdRegistry(System.getProperty("etcd.url"))
                .find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
            for (Map.Entry<Endpoint, Integer> entry : map.entrySet()) {
                channelSize += entry.getValue();
            }
            channels = new Channel[channelSize];

            for (Map.Entry<Endpoint, Integer> entry : map.entrySet()) {
                Channel channel = bootstrap.connect(entry.getKey().getHost(), entry.getKey().getPort()).sync().channel();
                for (int i = 0; i < entry.getValue(); i++) {
                    channels[chanelIndex] = channel;
                    chanelIndex++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Channel getChannel() {
        return channels[atomicLong.updateAndGet(e -> (channels.length - 1 > e ? e + 1 : 0))];
    }

    private void initBootstrap() {

        bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup(4))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new ConsumerClientInitializer());
    }
}
