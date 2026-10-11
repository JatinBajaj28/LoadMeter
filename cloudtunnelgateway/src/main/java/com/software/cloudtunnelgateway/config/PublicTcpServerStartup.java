package com.software.cloudtunnelgateway.config;

import com.software.cloudtunnelgateway.tunnel.PublicTcpServer;
import com.software.cloudtunnelgateway.tunnel.StreamRegistry;
import com.software.cloudtunnelgateway.tunnel.TunnelRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class PublicTcpServerStartup {

    private final PublicTcpServer publicTcpServer;

    public PublicTcpServerStartup(
            TunnelRegistry tunnelRegistry,
            StreamRegistry streamRegistry
    ) {
        this.publicTcpServer = new PublicTcpServer(
                9100,
                tunnelRegistry,
                streamRegistry
        );
    }

    @PostConstruct
    public void start() {
        publicTcpServer.start();
    }

    @PreDestroy
    public void stop() {
        publicTcpServer.stop();
    }
}