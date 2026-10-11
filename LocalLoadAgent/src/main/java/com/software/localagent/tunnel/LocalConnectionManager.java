package com.software.localagent.tunnel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class LocalConnectionManager {

    private final EventLoopGroup eventLoopGroup;

    private final Map<Long, Channel> localChannels =
            new ConcurrentHashMap<>();

    public LocalConnectionManager() {
        this.eventLoopGroup = new NioEventLoopGroup();
    }

    public void openConnection(
            long streamId,
            int localPort,
            Channel tunnelChannel,
            Consumer<Channel> onSuccess,
            Consumer<Throwable> onFailure
    ) {

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        5000
                )
                .handler(
                        new LocalChannelInitializer(
                                streamId,
                                localChannels,
                                tunnelChannel
                        )
                );

        bootstrap.connect(
                "127.0.0.1",
                localPort
        ).addListener(future -> {

            if (future.isSuccess()) {

                Channel channel =
                        ((io.netty.channel.ChannelFuture) future)
                                .channel();

                localChannels.put(streamId, channel);

                System.out.println(
                        "Local connection established. "
                                + "streamId="
                                + streamId
                );

                onSuccess.accept(channel);

            } else {

                Throwable cause = future.cause();

                System.err.println(
                        "Failed to connect to localhost:"
                                + localPort
                                + " for stream "
                                + streamId
                );

                onFailure.accept(cause);
            }
        });
    }

    public Channel getChannel(long streamId) {
        return localChannels.get(streamId);
    }

    public void closeConnection(long streamId) {

        Channel channel =
                localChannels.remove(streamId);

        if (channel != null) {
            channel.close();
        }
    }

    public void shutdown() {
        eventLoopGroup.shutdownGracefully();
    }
}