package com.software.localagent.tunnel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;

public class LocalChannelInitializer
        extends ChannelInitializer<SocketChannel> {

    private final long streamId;

    private final Map<Long, Channel> localChannels;

    private final Channel tunnelChannel;

    public LocalChannelInitializer(
            long streamId,
            Map<Long, Channel> localChannels,
            Channel tunnelChannel
    ) {
        this.streamId = streamId;
        this.localChannels = localChannels;
        this.tunnelChannel = tunnelChannel;
    }

    @Override
    protected void initChannel(SocketChannel channel) {

        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(
                "handler",
                new LocalChannelHandler(
                        streamId,
                        localChannels,
                        tunnelChannel
                )
        );
    }
}