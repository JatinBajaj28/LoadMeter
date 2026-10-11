package com.software.cloudtunnelgateway.tunnel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

public class PublicTcpChannelHandler
        extends ChannelInboundHandlerAdapter {

    private final TunnelRegistry tunnelRegistry;
    private final StreamRegistry streamRegistry;
    private final AtomicLong streamIdGenerator;

    private final Queue<byte[]> pendingData = new ArrayDeque<>();

    private long streamId = -1;
    private Channel agentChannel;
    private boolean streamReady = false;

    public PublicTcpChannelHandler(
            TunnelRegistry tunnelRegistry,
            StreamRegistry streamRegistry,
            AtomicLong streamIdGenerator
    ) {
        this.tunnelRegistry = tunnelRegistry;
        this.streamRegistry = streamRegistry;
        this.streamIdGenerator = streamIdGenerator;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        agentChannel = tunnelRegistry.getTunnel("agent-001");

        if (agentChannel == null || !agentChannel.isActive()) {
            System.err.println("No connected Agent available.");
            ctx.close();
            return;
        }

        streamId = streamIdGenerator.getAndIncrement();

        streamRegistry.register(streamId, ctx.channel());

        TunnelMessage message = new TunnelMessage(
                TunnelMessageType.OPEN_STREAM,
                streamId,
                null
        );

        agentChannel.writeAndFlush(message).addListener(future -> {
            if (!future.isSuccess()) {
                ctx.executor().execute(() -> {
                    streamRegistry.remove(streamId, ctx.channel());
                    ctx.close();
                });
            }
        });

        System.out.println("OPEN_STREAM sent. streamId=" + streamId);
    }

    @Override
    public void channelRead(
            ChannelHandlerContext ctx,
            Object msg
    ) {
        if (!(msg instanceof ByteBuf buffer)) {
            return;
        }

        try {
            byte[] data = new byte[buffer.readableBytes()];
            buffer.readBytes(data);

            if (!streamReady) {
                // Prevent unbounded buffering while waiting for the Agent.
                if (pendingData.size() >= 100) {
                    System.err.println("Too much pending data; closing client.");
                    ctx.close();
                    return;
                }

                pendingData.add(data);
                return;
            }

            sendData(data);

        } finally {
            buffer.release();
        }
    }

    private void sendData(byte[] data) {

        if (agentChannel == null || !agentChannel.isActive()) {
            return;
        }

        TunnelMessage message = new TunnelMessage(
                TunnelMessageType.DATA,
                streamId,
                data
        );

        agentChannel.writeAndFlush(message);
    }

    public void markStreamReady() {
        streamReady = true;

        while (!pendingData.isEmpty()) {
            sendData(pendingData.poll());
        }

        System.out.println("Stream ready. streamId=" + streamId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        if (streamId >= 0) {
            streamRegistry.remove(streamId, ctx.channel());

            if (agentChannel != null && agentChannel.isActive()) {
                agentChannel.writeAndFlush(
                        new TunnelMessage(
                                TunnelMessageType.CLOSE_STREAM,
                                streamId,
                                null
                        )
                );
            }
        }

        System.out.println(
                "Public client disconnected. streamId=" + streamId
        );
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx,
            Throwable cause
    ) {
        System.err.println("Public TCP connection error.");
        cause.printStackTrace();
        ctx.close();
    }
}