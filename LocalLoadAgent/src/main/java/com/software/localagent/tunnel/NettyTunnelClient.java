package com.software.localagent.tunnel;

import com.software.localagent.model.ConnectionStatus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyTunnelClient implements TunnelClient {

    private final TunnelConfiguration configuration;
    private final TunnelClientListener listener;

    private final EventLoopGroup eventLoopGroup;

    private Channel channel;

    private volatile ConnectionStatus status =
            ConnectionStatus.DISCONNECTED;

    public NettyTunnelClient(
            TunnelConfiguration configuration,
            TunnelClientListener listener
    ) {
        this.configuration = configuration;
        this.listener = listener;

        this.eventLoopGroup =
                new NioEventLoopGroup(1);
    }

    @Override
    public void connect(int localPort) {

        if (status == ConnectionStatus.CONNECTED ||
                status == ConnectionStatus.CONNECTING) {
            return;
        }

        status = ConnectionStatus.CONNECTING;

        listener.onStatusChanged(status);

        Bootstrap bootstrap =
                new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        5000
                )
                .handler(
                        new TunnelChannelInitializer(
                                configuration,
                                localPort,
                                listener,
                                this
                        )
                );

        bootstrap.connect(
                configuration.getGatewayHost(),
                configuration.getGatewayPort()
        ).addListener(future -> {

            if (!future.isSuccess()) {

                status =
                        ConnectionStatus.ERROR;

                listener.onStatusChanged(status);

                listener.onError(
                        future.cause()
                );

                return;
            }

            channel =
                    ((io.netty.channel.ChannelFuture) future)
                            .channel();

            System.out.println(
                    "Connected to tunnel gateway."
            );
        });
    }

    @Override
    public void disconnect() {

        if (channel != null) {

            channel.close();

            channel = null;
        }

        status =
                ConnectionStatus.DISCONNECTED;

        listener.onStatusChanged(status);
    }

    @Override
    public ConnectionStatus getStatus() {
        return status;
    }

    void updateStatus(
            ConnectionStatus status
    ) {

        this.status = status;

        listener.onStatusChanged(status);
    }
}