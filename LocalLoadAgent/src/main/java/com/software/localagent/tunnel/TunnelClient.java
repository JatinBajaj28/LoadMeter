package com.software.localagent.tunnel;

import com.software.localagent.model.ConnectionStatus;

public interface TunnelClient {

    void connect(int localPort);

    void disconnect();

    ConnectionStatus getStatus();
}