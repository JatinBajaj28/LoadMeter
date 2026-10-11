package com.software.cloudtunnelgateway.tunnel;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TunnelGatewayChannelHandler
        extends ChannelInboundHandlerAdapter {

    private final TunnelRegistry tunnelRegistry;
    private final StreamRegistry streamRegistry;

    private String agentId;

    public TunnelGatewayChannelHandler(
            TunnelRegistry tunnelRegistry,
            StreamRegistry streamRegistry
    ) {
        this.tunnelRegistry = tunnelRegistry;
        this.streamRegistry = streamRegistry;
    }

    @Override
    public void channelRead(
            ChannelHandlerContext ctx,
            Object msg
    ) {
        if (!(msg instanceof TunnelMessage message)) {
            return;
        }

        try {
            switch (message.getType()) {
                case REGISTER ->
                        handleRegister(ctx, message);

                case DATA ->
                        handleData(message);

                case OPEN_STREAM_ACK ->
                        handleOpenStreamAck(message);

                case CLOSE_STREAM ->
                        handleCloseStream(message);

                case PING ->
                        handlePing(ctx, message);

                case PONG ->
                        System.out.println("PONG received");

                default ->
                        System.out.println(
                                "Unhandled tunnel message: "
                                        + message.getType()
                        );
            }
        } catch (Exception e) {
            System.err.println(
                    "Error handling tunnel message: "
                            + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    private void handleRegister(
            ChannelHandlerContext ctx,
            TunnelMessage message
    ) {
        byte[] payload = message.getPayload();

        if (payload == null || payload.length == 0) {
            System.err.println("REGISTER has no agent ID");
            ctx.close();
            return;
        }

        agentId = new String(payload);

        tunnelRegistry.register(agentId, ctx.channel());

        TunnelMessage response = new TunnelMessage(
                TunnelMessageType.REGISTER_ACK,
                0,
                null
        );

        ctx.writeAndFlush(response);

        System.out.println("Agent registered: " + agentId);
        System.out.println("REGISTER_ACK sent to agent: " + agentId);
    }

    private void handleOpenStreamAck(TunnelMessage message) {
        long streamId = message.getStreamId();

        Channel publicChannel =
                streamRegistry.getChannel(streamId);

        if (publicChannel == null || !publicChannel.isActive()) {
            System.out.println(
                    "OPEN_STREAM_ACK received for inactive stream: "
                            + streamId
            );
            return;
        }

        publicChannel.eventLoop().execute(() -> {
            if (!publicChannel.isActive()) {
                return;
            }

            PublicTcpChannelHandler handler =
                    publicChannel.pipeline()
                            .get(PublicTcpChannelHandler.class);

            if (handler == null) {
                System.err.println(
                        "PublicTcpChannelHandler not found for stream: "
                                + streamId
                );
                publicChannel.close();
                return;
            }

            handler.markStreamReady();
        });

        System.out.println(
                "OPEN_STREAM_ACK received for stream: " + streamId
        );
    }

    private void handleData(TunnelMessage message) {
        long streamId = message.getStreamId();

        Channel publicChannel =
                streamRegistry.getChannel(streamId);

        if (publicChannel == null || !publicChannel.isActive()) {
            System.out.println(
                    "No active public connection for stream: "
                            + streamId
            );
            return;
        }

        byte[] payload = message.getPayload();

        if (payload == null || payload.length == 0) {
            return;
        }

        publicChannel.writeAndFlush(
                Unpooled.wrappedBuffer(payload)
        );
    }

    private void handleCloseStream(TunnelMessage message) {
        long streamId = message.getStreamId();

        Channel publicChannel =
                streamRegistry.getChannel(streamId);

        streamRegistry.remove(streamId);

        if (publicChannel != null && publicChannel.isActive()) {
            publicChannel.close();
        }

        System.out.println("Stream closed: " + streamId);
    }

    private void handlePing(
            ChannelHandlerContext ctx,
            TunnelMessage message
    ) {
        TunnelMessage response = new TunnelMessage(
                TunnelMessageType.PONG,
                message.getStreamId(),
                null
        );

        ctx.writeAndFlush(response);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (agentId != null) {
            tunnelRegistry.remove(agentId, ctx.channel());

            System.out.println(
                    "Agent disconnected: " + agentId
            );
        }
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx,
            Throwable cause
    ) {
        System.err.println(
                "Tunnel connection error: " + cause.getMessage()
        );

        ctx.close();
    }
}