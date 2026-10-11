package com.software.localagent.tunnel;

import com.software.localagent.model.ConnectionStatus;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;

import java.nio.charset.StandardCharsets;

public class TunnelChannelHandler
        extends ChannelInboundHandlerAdapter {

    private final TunnelConfiguration configuration;
    private final int localPort;
    private final TunnelClientListener listener;
    private final NettyTunnelClient tunnelClient;
    private final LocalConnectionManager localConnectionManager;

    public TunnelChannelHandler(
            TunnelConfiguration configuration,
            int localPort,
            TunnelClientListener listener,
            NettyTunnelClient tunnelClient
    ) {
        this.configuration = configuration;
        this.localPort = localPort;
        this.listener = listener;
        this.tunnelClient = tunnelClient;
        this.localConnectionManager = new LocalConnectionManager();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("TCP tunnel connection established.");

        if (!configuration.isTlsEnabled()) {
            sendRegister(ctx);
        }
    }

    private void sendRegister(ChannelHandlerContext ctx) {
        String agentId = configuration.getAgentId();

        byte[] agentIdBytes =
                agentId.getBytes(StandardCharsets.UTF_8);

        TunnelMessage registerMessage = new TunnelMessage(
                TunnelMessageType.REGISTER,
                0,
                agentIdBytes
        );

        ctx.writeAndFlush(registerMessage);

        System.out.println(
                "REGISTER sent for agent: " + agentId
        );
    }

    @Override
    public void userEventTriggered(
            ChannelHandlerContext ctx,
            Object event
    ) throws Exception {

        if (event instanceof SslHandshakeCompletionEvent sslEvent) {
            if (sslEvent.isSuccess()) {
                System.out.println("TLS handshake completed.");
                sendRegister(ctx);
            } else {
                System.err.println("TLS handshake failed.");

                tunnelClient.updateStatus(ConnectionStatus.ERROR);
                listener.onError(sslEvent.cause());
                ctx.close();
            }

            return;
        }

        ctx.fireUserEventTriggered(event);
    }

    @Override
    public void channelRead(
            ChannelHandlerContext ctx,
            Object msg
    ) {
        if (!(msg instanceof TunnelMessage message)) {
            System.err.println(
                    "Unexpected tunnel message type: "
                            + msg.getClass().getName()
            );
            return;
        }

        System.out.println(
                "Received tunnel message: "
                        + message.getType()
                        + ", streamId="
                        + message.getStreamId()
        );

        switch (message.getType()) {

            case REGISTER_ACK -> {
                System.out.println(
                        "Tunnel registration accepted."
                );

                tunnelClient.updateStatus(
                        ConnectionStatus.CONNECTED
                );
            }

            case OPEN_STREAM -> {
                long streamId = message.getStreamId();

                System.out.println(
                        "Gateway requested new stream: " + streamId
                );

                localConnectionManager.openConnection(
                        streamId,
                        localPort,
                        ctx.channel(),

                        channel -> {
                            System.out.println(
                                    "Local connection ready. "
                                            + "Sending OPEN_STREAM_ACK. "
                                            + "streamId=" + streamId
                            );

                            TunnelMessage ack = new TunnelMessage(
                                    TunnelMessageType.OPEN_STREAM_ACK,
                                    streamId,
                                    null
                            );

                            ctx.writeAndFlush(ack);
                        },

                        error -> {
                            System.err.println(
                                    "Failed to open local connection. "
                                            + "streamId=" + streamId
                                            + ", error=" + error.getMessage()
                            );

                            TunnelMessage close = new TunnelMessage(
                                    TunnelMessageType.CLOSE_STREAM,
                                    streamId,
                                    null
                            );

                            ctx.writeAndFlush(close);
                        }
                );
            }

            case OPEN_STREAM_ACK -> {
                System.out.println(
                        "Stream opened successfully. streamId="
                                + message.getStreamId()
                );
            }

            case DATA -> {
                long streamId = message.getStreamId();

                Channel localChannel =
                        localConnectionManager.getChannel(streamId);

                if (localChannel == null || !localChannel.isActive()) {
                    System.err.println(
                            "No active local connection for streamId="
                                    + streamId
                    );
                    return;
                }

                byte[] payload = message.getPayload();

                if (payload == null || payload.length == 0) {
                    return;
                }

                System.out.println(
                        "Received DATA from Gateway: "
                                + payload.length
                                + " bytes, streamId=" + streamId
                );

                localChannel.writeAndFlush(
                        Unpooled.wrappedBuffer(payload)
                ).addListener(future -> {
                    if (future.isSuccess()) {
                        System.out.println(
                                "Forwarded DATA to localhost:"
                                        + localPort
                                        + ", streamId=" + streamId
                        );
                    } else {
                        System.err.println(
                                "Failed to forward DATA to local app: "
                                        + future.cause()
                        );
                    }
                });
            }

            case CLOSE_STREAM -> {
                long streamId = message.getStreamId();

                System.out.println(
                        "Gateway closed stream: " + streamId
                );

                localConnectionManager.closeConnection(streamId);
            }

            case PING -> {
                System.out.println("Received PING.");

                TunnelMessage pong = new TunnelMessage(
                        TunnelMessageType.PONG,
                        message.getStreamId(),
                        null
                );

                ctx.writeAndFlush(pong);
            }

            case PONG -> System.out.println("Received PONG.");

            default -> System.out.println(
                    "Received message: " + message.getType()
            );
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        tunnelClient.updateStatus(ConnectionStatus.DISCONNECTED);
        localConnectionManager.shutdown();

        System.out.println("Tunnel connection closed.");
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx,
            Throwable cause
    ) {
        tunnelClient.updateStatus(ConnectionStatus.ERROR);
        listener.onError(cause);

        System.err.println("Tunnel handler error:");
        cause.printStackTrace();

        localConnectionManager.shutdown();
        ctx.close();
    }
}