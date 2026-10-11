package com.software.localagent.tunnel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TunnelMessageDecoder
        extends ByteToMessageDecoder {

    private static final int HEADER_SIZE = 13;

    private static final int MAX_PAYLOAD_SIZE =
            1024 * 1024; // 1 MB

    @Override
    protected void decode(
            ChannelHandlerContext ctx,
            ByteBuf in,
            List<Object> out
    ) {

        /*
         * Header:
         *
         * 1 byte  -> message type
         * 8 bytes -> stream ID
         * 4 bytes -> payload length
         *
         * Total = 13 bytes
         */
        if (in.readableBytes() < HEADER_SIZE) {
            return;
        }

        in.markReaderIndex();

        byte typeByte =
                in.readByte();

        long streamId =
                in.readLong();

        int payloadLength =
                in.readInt();

        /*
         * Validate payload length.
         */
        if (payloadLength < 0 ||
                payloadLength > MAX_PAYLOAD_SIZE) {

            System.err.println(
                    "Invalid payload length: "
                            + payloadLength
            );

            ctx.close();
            return;
        }

        /*
         * We have the header but perhaps
         * not the complete payload yet.
         */
        if (in.readableBytes() < payloadLength) {

            in.resetReaderIndex();

            return;
        }

        /*
         * Convert signed byte to unsigned value.
         *
         * Example:
         *
         * byte -1
         *     ↓
         * & 0xFF
         *     ↓
         * int 255
         */
        int typeIndex =
                typeByte & 0xFF;

        /*
         * Make sure the message type exists.
         */
        TunnelMessageType[] types =
                TunnelMessageType.values();

        if (typeIndex >= types.length) {

            System.err.println(
                    "Unknown tunnel message type: "
                            + typeIndex
            );

            ctx.close();
            return;
        }

        TunnelMessageType type =
                types[typeIndex];

        /*
         * Read payload.
         */
        byte[] payload =
                new byte[payloadLength];

        if (payloadLength > 0) {
            in.readBytes(payload);
        }

        /*
         * Construct the Java message.
         */
        TunnelMessage message =
                new TunnelMessage(
                        type,
                        streamId,
                        payload
                );



        out.add(message);
    }
}