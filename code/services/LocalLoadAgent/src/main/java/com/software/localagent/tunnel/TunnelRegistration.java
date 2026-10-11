package com.software.localagent.tunnel;

public class TunnelRegistration {

    private final String agentId;

    public TunnelRegistration(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentId() {
        return agentId;
    }
}