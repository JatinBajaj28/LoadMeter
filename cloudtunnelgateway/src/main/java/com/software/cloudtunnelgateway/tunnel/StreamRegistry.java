package com.software.cloudtunnelgateway.tunnel;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StreamRegistry {

    private final Map<Long, Channel> streams =
            new ConcurrentHashMap<>();

    public void register(
            long streamId,
            Channel channel
    ) {

        streams.put(
                streamId,
                channel
        );

        System.out.println(
                "Stream registered: "
                        + streamId
        );
    }

    public Channel getChannel(
            long streamId
    ) {

        return streams.get(streamId);
    }

    public void remove(
            long streamId
    ) {

        streams.remove(streamId);

        System.out.println(
                "Stream removed: "
                        + streamId
        );
    }

    public void remove(
            long streamId,
            Channel channel
    ) {

        streams.remove(
                streamId,
                channel
        );

        System.out.println(
                "Stream removed: "
                        + streamId
        );
    }

    public boolean contains(
            long streamId
    ) {

        return streams.containsKey(
                streamId
        );
    }

    public int size() {
        return streams.size();
    }
}