package com.software.localagent.tunnel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

public class LocalChannelHandler
        extends ChannelInboundHandlerAdapter {

    private final long streamId;
    private final Map<Long, Channel> localChannels;
    private final Channel tunnelChannel;

    public LocalChannelHandler(
            long streamId,
            Map<Long, Channel> localChannels,
            Channel tunnelChannel
    ) {
        this.streamId = streamId;
        this.localChannels = localChannels;
        this.tunnelChannel = tunnelChannel;
    }

    @Override
    public void channelRead(
            ChannelHandlerContext ctx,
            Object msg
    ) {

        if (!(msg instanceof ByteBuf buffer)) {
            return;
        }

        byte[] data = new byte[buffer.readableBytes()];

        buffer.readBytes(data);

        TunnelMessage message =
                new TunnelMessage(
                        TunnelMessageType.DATA,
                        streamId,
                        data
                );

        tunnelChannel.writeAndFlush(message);
    }

    @Override
    public void channelInactive(
            ChannelHandlerContext ctx
    ) {

        localChannels.remove(streamId);

        TunnelMessage message =
                new TunnelMessage(
                        TunnelMessageType.CLOSE_STREAM,
                        streamId,
                        null
                );

        if (tunnelChannel.isActive()) {
            tunnelChannel.writeAndFlush(message);
        }

        System.out.println(
                "Local connection closed. streamId="
                        + streamId
        );
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx,
            Throwable cause
    ) {

        cause.printStackTrace();

        localChannels.remove(streamId);

        if (tunnelChannel.isActive()) {

            TunnelMessage message =
                    new TunnelMessage(
                            TunnelMessageType.CLOSE_STREAM,
                            streamId,
                            null
                    );

            tunnelChannel.writeAndFlush(message);
        }

        ctx.close();
    }
}