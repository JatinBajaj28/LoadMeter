package com.software.cloudtunnelgateway.tunnel;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

@Component
public class TunnelGatewayServer {

    private final int port;

    private final TunnelRegistry tunnelRegistry;
    private final StreamRegistry streamRegistry;

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private Channel serverChannel;

    public TunnelGatewayServer(
            TunnelRegistry tunnelRegistry,
            StreamRegistry streamRegistry
    ) {
        this.port = 9090;

        this.tunnelRegistry = tunnelRegistry;
        this.streamRegistry = streamRegistry;

        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
    }

    public void start() {

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(
                        bossGroup,
                        workerGroup
                )
                .channel(NioServerSocketChannel.class)
                .childOption(
                        ChannelOption.SO_KEEPALIVE,
                        true
                )
                .childHandler(
                        new TunnelGatewayChannelInitializer(
                                tunnelRegistry,
                                streamRegistry
                        )
                );

        try {
            serverChannel = bootstrap.bind(port)
                    .sync()
                    .channel();

            System.out.println(
                    "Tunnel Gateway started on port " + port
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Failed to start tunnel gateway",
                    e
            );
        }
    }

    public StreamRegistry getStreamRegistry() {
        return streamRegistry;
    }

    public TunnelRegistry getTunnelRegistry() {
        return tunnelRegistry;
    }

    public void stop() {

        if (serverChannel != null) {
            serverChannel.close().syncUninterruptibly();
        }

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        System.out.println("Tunnel Gateway stopped.");
    }
}