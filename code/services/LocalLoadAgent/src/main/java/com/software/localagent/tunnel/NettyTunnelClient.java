
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

    // Backward-compatible method.
    // Existing callers can still connect to localhost.
    @Override
    public void connect(int localPort) {

        connect(
                new TargetConfiguration(
                        "127.0.0.1",
                        localPort
                )
        );
    }

    // Connect using the selected application destination.
    public void connect(
            TargetConfiguration targetConfiguration
    ) {

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
                                targetConfiguration,
                                listener,
                                this
                        )
                );

        bootstrap.connect(
                configuration.getGatewayHost(),
                configuration.getGatewayPort()
        ).addListener(future -> {

            if (!future.isSuccess()) {

                status = ConnectionStatus.ERROR;

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

            System.out.println(
                    "Configured target application: "
                            + targetConfiguration.getTargetHost()
                            + ":"
                            + targetConfiguration.getTargetPort()
            );
        });
    }

    @Override
    public void disconnect() {

        if (channel != null) {

            channel.close();

            channel = null;
        }

        status = ConnectionStatus.DISCONNECTED;

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
    public void shutdown() {

        if (channel != null) {
            channel.close();
            channel = null;
        }

        eventLoopGroup.shutdownGracefully();

        status = ConnectionStatus.DISCONNECTED;

        listener.onStatusChanged(status);

        System.out.println(
                "Netty tunnel client shut down."
        );
    }
}
