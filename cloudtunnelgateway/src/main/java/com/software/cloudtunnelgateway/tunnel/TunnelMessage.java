package com.software.cloudtunnelgateway.tunnel;

public class TunnelMessage {

    private final TunnelMessageType type;
    private final long streamId;
    private final byte[] payload;

    public TunnelMessage(
            TunnelMessageType type,
            long streamId,
            byte[] payload
    ) {
        this.type = type;
        this.streamId = streamId;
        this.payload = payload;
    }

    public TunnelMessageType getType() {
        return type;
    }

    public long getStreamId() {
        return streamId;
    }

    public byte[] getPayload() {
        return payload;
    }
}