package com.software.cloudtunnelgateway.tunnel;

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

        byte type =
                (byte) message.getType().ordinal();

        long streamId =
                message.getStreamId();

        byte[] payload =
                message.getPayload();

        int payloadLength =
                payload == null
                        ? 0
                        : payload.length;

        /*
         * Tunnel frame:
         *
         * 1 byte  -> message type
         * 8 bytes -> stream ID
         * 4 bytes -> payload length
         * N bytes -> payload
         */

        out.writeByte(type);

        out.writeLong(streamId);

        out.writeInt(payloadLength);

        if (payloadLength > 0) {
            out.writeBytes(payload);
        }
    }
}