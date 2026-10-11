package com.software.cloudtunnelgateway.tunnel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class TunnelGatewayChannelInitializer
        extends ChannelInitializer<SocketChannel> {

    private final TunnelRegistry tunnelRegistry;
    private final StreamRegistry streamRegistry;

    public TunnelGatewayChannelInitializer(
            TunnelRegistry tunnelRegistry,
            StreamRegistry streamRegistry
    ) {
        this.tunnelRegistry = tunnelRegistry;
        this.streamRegistry = streamRegistry;
    }

    @Override
    protected void initChannel(SocketChannel channel) {

        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(
                "encoder",
                new TunnelMessageEncoder()
        );

        pipeline.addLast(
                "decoder",
                new TunnelMessageDecoder()
        );

        pipeline.addLast(
                "handler",
                new TunnelGatewayChannelHandler(
                        tunnelRegistry,
                        streamRegistry
                )
        );
    }
}