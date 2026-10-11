package com.software.cloudtunnelgateway.tunnel;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class TunnelRegistry {

    private final Map<String, Channel> tunnels =
            new ConcurrentHashMap<>();

    public void register(
            String agentId,
            Channel channel
    ) {

        Channel previous =
                tunnels.put(agentId, channel);

        if (previous != null &&
                previous != channel &&
                previous.isActive()) {

            previous.close();
        }

        System.out.println(
                "Tunnel registered: "
                        + agentId
        );
    }

    public Channel getTunnel(
            String agentId
    ) {

        return tunnels.get(agentId);
    }

    public void remove(
            String agentId
    ) {

        tunnels.remove(agentId);

        System.out.println(
                "Tunnel removed: "
                        + agentId
        );
    }

    public void remove(
            String agentId,
            Channel channel
    ) {

        tunnels.remove(
                agentId,
                channel
        );

        System.out.println(
                "Tunnel removed: "
                        + agentId
        );
    }

    public boolean isConnected(
            String agentId
    ) {

        Channel channel =
                tunnels.get(agentId);

        return channel != null &&
                channel.isActive();
    }

    public int size() {
        return tunnels.size();
    }
}