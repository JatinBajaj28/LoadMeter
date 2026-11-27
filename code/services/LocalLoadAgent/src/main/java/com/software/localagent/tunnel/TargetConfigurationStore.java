
package com.software.localagent.tunnel;

import java.util.Optional;
import java.util.prefs.Preferences;

public final class TargetConfigurationStore {

    private static final String TARGET_HOST_KEY = "target.host";
    private static final String TARGET_PORT_KEY = "target.port";

    private static final Preferences PREFERENCES =
            Preferences.userNodeForPackage(
                    TargetConfigurationStore.class
            );

    private TargetConfigurationStore() {
        // Utility class
    }

    public static void save(TargetConfiguration configuration) {

        if (configuration == null) {
            throw new IllegalArgumentException(
                    "Target configuration cannot be null"
            );
        }

        PREFERENCES.put(
                TARGET_HOST_KEY,
                configuration.getTargetHost()
        );

        PREFERENCES.putInt(
                TARGET_PORT_KEY,
                configuration.getTargetPort()
        );

        try {
            PREFERENCES.flush();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not save target configuration.",
                    e
            );
        }
    }

    public static Optional<TargetConfiguration> load() {

        String host = PREFERENCES.get(
                TARGET_HOST_KEY,
                null
        );

        int port = PREFERENCES.getInt(
                TARGET_PORT_KEY,
                -1
        );

        if (host == null || host.isBlank() || port == -1) {
            return Optional.empty();
        }

        try {
            return Optional.of(
                    new TargetConfiguration(host, port)
            );
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static boolean isConfigured() {
        return load().isPresent();
    }

    public static void clear() {
        PREFERENCES.remove(TARGET_HOST_KEY);
        PREFERENCES.remove(TARGET_PORT_KEY);

        try {
            PREFERENCES.flush();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not clear target configuration.",
                    e
            );
        }
    }
}
