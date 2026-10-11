package com.software.localagent.tunnel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TunnelMessageEncoder
        extends MessageToByteEncoder<TunnelMessage> {

    @Override
    protected void encode(
            ChannelHandlerContext ctx,
            TunnelMessage message,
            ByteBuf out
    ) {

        byte type = (byte) message.getType().ordinal();

        long streamId = message.getStreamId();

        byte[] payload = message.getPayload();

        int payloadLength = payload == null
                ? 0
                : payload.length;

        // Message type
        out.writeByte(type);

        // Stream identifier
        out.writeLong(streamId);

        // Payload length
        out.writeInt(payloadLength);

        // Payload
        if (payloadLength > 0) {
            out.writeBytes(payload);
        }
    }
}