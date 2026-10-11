package com.software.localagent.tunnel;

public class TunnelConfiguration {

    private final String gatewayHost;
    private final int gatewayPort;
    private final boolean tlsEnabled;
    private final String agentId;

    public TunnelConfiguration(
            String gatewayHost,
            int gatewayPort,
            boolean tlsEnabled,
            String agentId
    ) {
        this.gatewayHost = gatewayHost;
        this.gatewayPort = gatewayPort;
        this.tlsEnabled = tlsEnabled;
        this.agentId = agentId;
    }

    public String getGatewayHost() {
        return gatewayHost;
    }

    public int getGatewayPort() {
        return gatewayPort;
    }

    public boolean isTlsEnabled() {
        return tlsEnabled;
    }

    public String getAgentId() {
        return agentId;
    }
}