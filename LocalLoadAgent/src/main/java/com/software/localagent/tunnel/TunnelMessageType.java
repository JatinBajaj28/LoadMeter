package com.software.localagent.tunnel;

public enum TunnelMessageType {

    REGISTER,
    REGISTER_ACK,

    OPEN_STREAM,
    OPEN_STREAM_ACK,

    DATA,
    CLOSE_STREAM,

    PING,
    PONG
}