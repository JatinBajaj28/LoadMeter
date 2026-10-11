package com.software.localagent.ui;

import com.software.localagent.model.ConnectionStatus;
import com.software.localagent.tunnel.TunnelClient;
import com.software.localagent.tunnel.TunnelClientListener;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ConnectionView extends VBox
        implements TunnelClientListener {

    private final TextField portField;
    private final Label statusLabel;
    private final Button connectButton;

    private TunnelClient tunnelClient;

    public ConnectionView() {

        setSpacing(20);
        setPadding(new Insets(40));
        setAlignment(Pos.CENTER);

        Label title = new Label("LocalLoad Agent");

        title.setStyle("""
                -fx-font-size: 28px;
                -fx-font-weight: bold;
                """);

        Label subtitle = new Label(
                "Expose your local application through a secure L4 tunnel"
        );

        subtitle.setStyle("""
                -fx-font-size: 14px;
                -fx-text-fill: #666666;
                """);

        Label portLabel =
                new Label("Local Application Port");

        portField =
                new TextField("8080");

        portField.setMaxWidth(250);
        portField.setPromptText("e.g. 8080");

        connectButton =
                new Button("CONNECT");

        connectButton.setPrefWidth(250);
        connectButton.setPrefHeight(40);

        statusLabel =
                new Label();

        setStatus(
                ConnectionStatus.DISCONNECTED
        );

        connectButton.setOnAction(event -> {

            if (tunnelClient == null) {
                return;
            }

            try {

                int port = getPort();

                setStatus(
                        ConnectionStatus.CONNECTING
                );

                tunnelClient.connect(port);

            } catch (NumberFormatException e) {

                statusLabel.setText(
                        "● Invalid port"
                );

                statusLabel.setStyle(
                        "-fx-text-fill: #dc2626;"
                );
            }
        });

        VBox portBox =
                new VBox(8);

        portBox.setAlignment(Pos.CENTER);

        portBox.getChildren().addAll(
                portLabel,
                portField
        );

        getChildren().addAll(
                title,
                subtitle,
                portBox,
                connectButton,
                statusLabel
        );
    }

    public void setTunnelClient(
            TunnelClient tunnelClient
    ) {
        this.tunnelClient =
                tunnelClient;
    }

    public int getPort() {

        return Integer.parseInt(
                portField.getText().trim()
        );
    }

    public Button getConnectButton() {
        return connectButton;
    }

    @Override
    public void onStatusChanged(
            ConnectionStatus status
    ) {

        Platform.runLater(
                () -> setStatus(status)
        );
    }

    @Override
    public void onPublicEndpointAssigned(
            String publicEndpoint
    ) {

        Platform.runLater(() -> {

            System.out.println(
                    "Public endpoint: "
                            + publicEndpoint
            );
        });
    }

    @Override
    public void onError(
            Throwable error
    ) {

        Platform.runLater(() -> {

            setStatus(
                    ConnectionStatus.ERROR
            );

            System.err.println(
                    "Tunnel error: "
                            + error.getMessage()
            );
        });
    }

    public void setStatus(
            ConnectionStatus status
    ) {

        switch (status) {

            case DISCONNECTED -> {

                statusLabel.setText(
                        "● Disconnected"
                );

                statusLabel.setStyle(
                        "-fx-text-fill: #777777;"
                );

                connectButton.setText(
                        "CONNECT"
                );

                connectButton.setDisable(false);
            }

            case CONNECTING -> {

                statusLabel.setText(
                        "● Connecting..."
                );

                statusLabel.setStyle(
                        "-fx-text-fill: #d97706;"
                );

                connectButton.setText(
                        "CONNECTING..."
                );

                connectButton.setDisable(true);
            }

            case CONNECTED -> {

                statusLabel.setText(
                        "● Connected"
                );

                statusLabel.setStyle(
                        "-fx-text-fill: #16a34a;"
                );

                connectButton.setText(
                        "CONNECTED"
                );

                connectButton.setDisable(true);
            }

            case RECONNECTING -> {

                statusLabel.setText(
                        "● Reconnecting..."
                );

                statusLabel.setStyle(
                        "-fx-text-fill: #d97706;"
                );

                connectButton.setText(
                        "RECONNECTING..."
                );

                connectButton.setDisable(true);
            }

            case ERROR -> {

                statusLabel.setText(
                        "● Connection Error"
                );

                statusLabel.setStyle(
                        "-fx-text-fill: #dc2626;"
                );

                connectButton.setText(
                        "RETRY"
                );

                connectButton.setDisable(false);
            }
        }
    }
}