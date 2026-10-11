package com.software.cloudtunnelgateway.tunnel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.atomic.AtomicLong;

public class PublicTcpChannelInitializer
        extends ChannelInitializer<SocketChannel> {

    private final TunnelRegistry tunnelRegistry;
    private final StreamRegistry streamRegistry;
    private final AtomicLong streamIdGenerator;

    public PublicTcpChannelInitializer(
            TunnelRegistry tunnelRegistry,
            StreamRegistry streamRegistry,
            AtomicLong streamIdGenerator
    ) {
        this.tunnelRegistry = tunnelRegistry;
        this.streamRegistry = streamRegistry;
        this.streamIdGenerator = streamIdGenerator;
    }

    @Override
    protected void initChannel(SocketChannel channel) {

        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(
                "handler",
                new PublicTcpChannelHandler(
                        tunnelRegistry,
                        streamRegistry,
                        streamIdGenerator
                )
        );
    }
}