
package com.software.localagent;

import com.software.localagent.api.LoadTestApiClient;
import com.software.localagent.tunnel.NettyTunnelClient;
import com.software.localagent.tunnel.TunnelConfiguration;
import com.software.localagent.ui.ConnectionView;
import com.software.localagent.ui.LoadTestView;
import com.software.localagent.ui.TestExecutionView;
import com.software.localagent.ui.TestHistoryView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.util.Objects;

public class LocalAgentApplication extends Application {

    private NettyTunnelClient tunnelClient;
    private LoadTestView loadTestView;

    @Override
    public void start(Stage stage) {

        /*
         * Gateway configuration.
         *
         * Local development:
         * Tunnel listener: localhost:9090
         * Public TCP listener: localhost:9100
         * TLS: disabled for local testing.
         */
        TunnelConfiguration configuration =
                new TunnelConfiguration(
                        "localhost",
                        9090,
                        false,
                        "agent-001"
                );

        /*
         * Connection screen.
         */
        ConnectionView connectionView =
                new ConnectionView();

        /*
         * Netty tunnel client.
         */
        tunnelClient =
                new NettyTunnelClient(
                        configuration,
                        connectionView
                );

        connectionView.setTunnelClient(tunnelClient);

        /*
         * Backend REST API client.
         */
        LoadTestApiClient apiClient =
                new LoadTestApiClient();

        /*
         * Load test screen.
         */
        loadTestView =
                new LoadTestView(
                        apiClient,
                        "localhost",
                        tunnelClient
                );

        /*
         * Main navigation tabs.
         */
        Tab connectionTab = new Tab(
                "Connection",
                connectionView
        );

        Tab loadTestTab = new Tab(
                "Load Test",
                loadTestView
        );

        /*
         * Main tab pane.
         */
        TabPane tabPane = new TabPane(
                connectionTab,
                loadTestTab
        );

        connectionTab.setClosable(false);
        loadTestTab.setClosable(false);

        /*
         * Test history screen.
         *
         * Double-clicking a history record opens
         * its execution monitor in a new tab.
         */
        TestHistoryView historyView =
                new TestHistoryView(
                        apiClient,
                        testId -> openHistoricalTest(
                                tabPane,
                                apiClient,
                                testId
                        )
                );

        Tab historyTab = new Tab(
                "Test History",
                historyView
        );

        historyTab.setClosable(false);

        tabPane.getTabs().add(historyTab);

        /*
         * Main application scene.
         */
        Scene scene = new Scene(
                tabPane,
                1100,
                700
        );

        tabPane.getStyleClass().add("main-navigation");
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/theme.css")
        ).toExternalForm());

        stage.setTitle("Testmyload - Local Agent");
        stage.setScene(scene);

        stage.setMinWidth(850);
        stage.setMinHeight(600);

        stage.show();
    }

    /*
     * Open an existing test from history.
     */
    private void openHistoricalTest(
            TabPane tabPane,
            LoadTestApiClient apiClient,
            String testId
    ) {

        String tabTitle =
                "Test: " + testId.substring(
                        0,
                        Math.min(8, testId.length())
                );

        /*
         * Avoid opening duplicate monitor tabs
         * for the same test.
         */
        for (Tab tab : tabPane.getTabs()) {

            if (tab.getUserData() instanceof String existingId
                    && existingId.equals(testId)) {

                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        TestExecutionView executionView =
                new TestExecutionView(
                        apiClient,
                        testId
                );

        Tab executionTab = new Tab(
                tabTitle,
                executionView
        );

        executionTab.setUserData(testId);

        executionTab.setClosable(true);

        /*
         * Stop polling when the monitor tab closes.
         */
        executionTab.setOnClosed(event ->
                executionView.stopMonitoring()
        );

        tabPane.getTabs().add(executionTab);

        tabPane.getSelectionModel().select(
                executionTab
        );
    }

    /*
     * Clean application shutdown.
     */
    @Override
    public void stop() {

        if (loadTestView != null) {
            loadTestView.stopMonitoring();
        }

        if (tunnelClient != null) {

            System.out.println(
                    "Shutting down Testmyload Agent..."
            );

            tunnelClient.shutdown();

            System.out.println(
                    "Testmyload Agent shutdown complete."
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
