
package com.software.localagent.tunnel;


import com.software.localagent.model.ConnectionStatus;

public interface TunnelClient {

    void connect(int localPort);

    void connect(TargetConfiguration targetConfiguration);

    void disconnect();

    void shutdown();

    ConnectionStatus getStatus();
}

