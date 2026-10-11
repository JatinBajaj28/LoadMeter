package com.software.localagent.tunnel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

public class TunnelChannelInitializer
        extends ChannelInitializer<SocketChannel> {

    private final TunnelConfiguration configuration;
    private final int localPort;
    private final TunnelClientListener listener;
    private final NettyTunnelClient tunnelClient;

    public TunnelChannelInitializer(
            TunnelConfiguration configuration,
            int localPort,
            TunnelClientListener listener,
            NettyTunnelClient tunnelClient
    ) {
        this.configuration = configuration;
        this.localPort = localPort;
        this.listener = listener;
        this.tunnelClient = tunnelClient;
    }

    @Override
    protected void initChannel(
            SocketChannel channel
    ) throws Exception {

        ChannelPipeline pipeline =
                channel.pipeline();

        /*
         * TLS must be added BEFORE our tunnel
         * protocol encoder/decoder.
         *
         * TCP
         *   ↓
         * TLS
         *   ↓
         * Tunnel Protocol
         */
        if (configuration.isTlsEnabled()) {

            SslContext sslContext =
                    SslContextBuilder
                            .forClient()
                            .build();

            pipeline.addLast(
                    "ssl",
                    sslContext.newHandler(
                            channel.alloc(),
                            configuration.getGatewayHost(),
                            configuration.getGatewayPort()
                    )
            );
        }

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
                new TunnelChannelHandler(
                        configuration,
                        localPort,
                        listener,
                        tunnelClient
                )
        );
    }
}