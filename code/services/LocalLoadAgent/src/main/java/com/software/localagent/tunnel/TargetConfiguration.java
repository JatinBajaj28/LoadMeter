
package com.software.localagent.tunnel;

public class TargetConfiguration {

    private final String targetHost;
    private final int targetPort;

    public TargetConfiguration(String targetHost, int targetPort) {

        if (targetHost == null || targetHost.isBlank()) {
            throw new IllegalArgumentException(
                    "Target host cannot be empty"
            );
        }

        if (targetPort < 1 || targetPort > 65535) {
            throw new IllegalArgumentException(
                    "Target port must be between 1 and 65535"
            );
        }

        this.targetHost = targetHost.trim();
        this.targetPort = targetPort;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public int getTargetPort() {
        return targetPort;
    }
}
