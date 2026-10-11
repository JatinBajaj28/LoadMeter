package com.software.localagent;

import com.software.localagent.tunnel.NettyTunnelClient;
import com.software.localagent.tunnel.TunnelConfiguration;
import com.software.localagent.ui.ConnectionView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LocalAgentApplication extends Application {

    @Override
    public void start(Stage stage) {

        /*
         * Gateway configuration
         *
         * Gateway: localhost:9090
         * TLS: disabled for the first local test
         */
        TunnelConfiguration configuration =
                new TunnelConfiguration(
                        "localhost",
                        9090,
                        false,
                        "agent-001"
                );

        /*
         * Create the JavaFX connection screen.
         */
        ConnectionView connectionView =
                new ConnectionView();

        /*
         * Create the Netty tunnel client.
         *
         * ConnectionView implements
         * TunnelClientListener, so it receives
         * connection status updates.
         */
        NettyTunnelClient tunnelClient =
                new NettyTunnelClient(
                        configuration,
                        connectionView
                );

        /*
         * Connect the UI to the tunnel client.
         */
        connectionView.setTunnelClient(
                tunnelClient
        );

        Scene scene =
                new Scene(
                        connectionView,
                        800,
                        500
                );

        stage.setTitle(
                "LocalLoad Agent"
        );

        stage.setScene(scene);

        stage.setMinWidth(700);
        stage.setMinHeight(450);

        stage.show();
    }

    @Override
    public void stop() {

        /*
         * Later we'll properly close the
         * Netty EventLoopGroup here.
         */
    }

    public static void main(String[] args) {
        launch(args);
    }
}