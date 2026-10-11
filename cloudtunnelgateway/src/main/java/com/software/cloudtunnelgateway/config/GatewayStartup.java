package com.software.cloudtunnelgateway.config;

import com.software.cloudtunnelgateway.tunnel.TunnelGatewayServer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class GatewayStartup {

    private final TunnelGatewayServer tunnelGatewayServer;

    public GatewayStartup(
            TunnelGatewayServer tunnelGatewayServer
    ) {
        this.tunnelGatewayServer =
                tunnelGatewayServer;
    }

    @PostConstruct
    public void startGateway() {

        tunnelGatewayServer.start();
    }
}