package com.software.cloudtunnelgateway.tunnel;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.atomic.AtomicLong;

public class PublicTcpServer {

    private final int port;

    private final TunnelRegistry tunnelRegistry;
    private final StreamRegistry streamRegistry;

    private final AtomicLong streamIdGenerator =
            new AtomicLong(1);

    private final EventLoopGroup bossGroup =
            new NioEventLoopGroup(1);

    private final EventLoopGroup workerGroup =
            new NioEventLoopGroup();

    private Channel serverChannel;

    public PublicTcpServer(
            int port,
            TunnelRegistry tunnelRegistry,
            StreamRegistry streamRegistry
    ) {
        this.port = port;
        this.tunnelRegistry = tunnelRegistry;
        this.streamRegistry = streamRegistry;
    }

    public void start() {

        ServerBootstrap bootstrap =
                new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(
                        new PublicTcpChannelInitializer(
                                tunnelRegistry,
                                streamRegistry,
                                streamIdGenerator
                        )
                );

        try {
            serverChannel = bootstrap.bind(port)
                    .sync()
                    .channel();

            System.out.println(
                    "Public TCP server started on port " + port
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Failed to start public TCP server",
                    e
            );
        }
    }

    public void stop() {

        if (serverChannel != null) {
            serverChannel.close();
        }

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        System.out.println("Public TCP server stopped.");
    }
}