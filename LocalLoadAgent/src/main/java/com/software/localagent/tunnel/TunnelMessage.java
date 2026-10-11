package com.software.localagent.tunnel;

public class TunnelMessage {

    private TunnelMessageType type;
    private long streamId;
    private byte[] payload;

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