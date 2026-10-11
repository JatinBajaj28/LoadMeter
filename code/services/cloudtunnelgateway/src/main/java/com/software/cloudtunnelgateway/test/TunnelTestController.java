package com.software.cloudtunnelgateway.test;

import com.software.cloudtunnelgateway.tunnel.TunnelMessage;
import com.software.cloudtunnelgateway.tunnel.TunnelMessageType;
import com.software.cloudtunnelgateway.tunnel.TunnelRegistry;
import io.netty.channel.Channel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/test/tunnel")
public class TunnelTestController {

    private final TunnelRegistry tunnelRegistry;

    private final AtomicLong streamIdGenerator =
            new AtomicLong(1);

    public TunnelTestController(
            TunnelRegistry tunnelRegistry
    ) {
        this.tunnelRegistry = tunnelRegistry;
    }

    @PostMapping("/open/{agentId}")
    public ResponseEntity<String> openStream(
            @PathVariable String agentId
    ) {

        Channel agentChannel =
                tunnelRegistry.getTunnel(agentId);

        if (agentChannel == null ||
                !agentChannel.isActive()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Agent is not connected: "
                                    + agentId
                    );
        }

        long streamId =
                streamIdGenerator.getAndIncrement();

        TunnelMessage message =
                new TunnelMessage(
                        TunnelMessageType.OPEN_STREAM,
                        streamId,
                        null
                );

        agentChannel.writeAndFlush(message);

        System.out.println(
                "OPEN_STREAM sent. "
                        + "agentId="
                        + agentId
                        + ", streamId="
                        + streamId
        );

        return ResponseEntity.ok(
                "OPEN_STREAM sent. "
                        + "streamId="
                        + streamId
        );
    }
}