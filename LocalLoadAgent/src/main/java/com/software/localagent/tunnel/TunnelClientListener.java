package com.software.localagent.tunnel;

import com.software.localagent.model.ConnectionStatus;

public interface TunnelClientListener {

    void onStatusChanged(ConnectionStatus status);

    void onPublicEndpointAssigned(String publicEndpoint);

    void onError(Throwable error);
}