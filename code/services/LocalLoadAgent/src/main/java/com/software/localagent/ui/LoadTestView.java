
package com.software.localagent.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.software.localagent.api.LoadTestApiClient;
import com.software.localagent.model.LoadTestConfiguration;
import com.software.localagent.tunnel.TunnelClient;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedHashMap;

public class LoadTestView extends VBox {

    private final LoadTestApiClient apiClient;
    private final String gatewayHost;
    private final TunnelClient tunnelClient;

    private final TextField targetUrlField;
    private final ComboBox<String> methodField;
    private final TextField totalRequestsField;
    private final TextField concurrencyField;
    private final TextField timeoutField;
    private final TextField expectedStatusField;
    private final TextArea headersField;
    private final TextArea queryParamsField;
    private final TextArea bodyField;
    private final TextArea expectedResponseBodyField;

    private final CheckBox includeRequestDetailsCheckBox;

    private final Button startButton;
    private final Label connectionStatusLabel;
    private final Label statusLabel;
    private final Label submissionMessage;

    private final Timeline connectionMonitor;

    private final TabPane internalTabs;
    private final Tab configurationTab;
    private final Tab executionTab;

    private boolean submitting = false;

    private TestExecutionView currentExecutionView;

    public LoadTestView(
            LoadTestApiClient apiClient,
            String gatewayHost,
            TunnelClient tunnelClient
    ) {

        if (apiClient == null) {
            throw new IllegalArgumentException(
                    "API client cannot be null"
            );
        }

        if (gatewayHost == null || gatewayHost.isBlank()) {
            throw new IllegalArgumentException(
                    "Cloud gateway host cannot be empty"
            );
        }

        if (tunnelClient == null) {
            throw new IllegalArgumentException(
                    "Tunnel client cannot be null"
            );
        }

        this.apiClient = apiClient;
        this.gatewayHost = gatewayHost;
        this.tunnelClient = tunnelClient;

        setPadding(new Insets(10));

        Label title = new Label("Create Load Test");

        title.setStyle("""
                -fx-font-size: 24px;
                -fx-font-weight: bold;
                """);

        Label subtitle = new Label(
                "Configure traffic for your local application"
        );

        connectionStatusLabel = new Label();

        targetUrlField = new TextField(
                "http://localhost:8080/api/users"
        );

        methodField = new ComboBox<>();
        methodField.getItems().addAll(
                "GET",
                "POST",
                "PUT",
                "DELETE"
        );
        methodField.setValue("GET");

        totalRequestsField = new TextField("100");
        concurrencyField = new TextField("10");
        timeoutField = new TextField("5000");
        expectedStatusField = new TextField("200");

        headersField = new TextArea();
        headersField.setPromptText("One header per line, e.g. Content-Type: application/json");
        headersField.setPrefRowCount(3);

        queryParamsField = new TextArea();
        queryParamsField.setPromptText("One parameter per line, e.g. page=1");
        queryParamsField.setPrefRowCount(2);

        bodyField = new TextArea();
        bodyField.setPromptText("Raw request body (JSON, form data, or text)");
        bodyField.setPrefRowCount(5);
        bodyField.disableProperty().bind(methodField.valueProperty().isEqualTo("GET"));

        expectedResponseBodyField = new TextArea();
        expectedResponseBodyField.setPromptText("Case-sensitive text the response must contain (optional)");
        expectedResponseBodyField.setPrefRowCount(3);

        includeRequestDetailsCheckBox =
                new CheckBox("Store individual request details");

        includeRequestDetailsCheckBox.setSelected(false);

        GridPane form = new GridPane();

        form.getStyleClass().add("surface");
        form.setHgap(15);
        form.setVgap(12);

        form.addRow(
                0,
                new Label("Target URL"),
                targetUrlField
        );

        form.addRow(
                1,
                new Label("HTTP Method"),
                methodField
        );

        form.addRow(
                2,
                new Label("Total Requests"),
                totalRequestsField
        );

        form.addRow(
                3,
                new Label("Concurrency"),
                concurrencyField
        );

        form.addRow(
                4,
                new Label("Timeout (ms)"),
                timeoutField
        );

        form.addRow(
                5,
                new Label("Expected Status Code"),
                expectedStatusField
        );

        form.add(
                includeRequestDetailsCheckBox,
                0,
                6,
                2,
                1
        );

        form.addRow(7, new Label("Request Headers"), headersField);
        form.addRow(8, new Label("Query Parameters"), queryParamsField);
        form.addRow(9, new Label("Request Body"), bodyField);
        form.addRow(10, new Label("Expected Response Body"), expectedResponseBodyField);

        targetUrlField.setPrefWidth(350);
        GridPane.setHgrow(targetUrlField, Priority.ALWAYS);

        startButton = new Button("START LOAD TEST");
        startButton.getStyleClass().add("primary-button");
        startButton.setPrefWidth(220);
        startButton.setPrefHeight(40);

        statusLabel = new Label("Ready to configure test");

        submissionMessage = new Label();
        submissionMessage.getStyleClass().add("muted");
        submissionMessage.setWrapText(true);
        submissionMessage.managedProperty().bind(submissionMessage.textProperty().isNotEmpty());
        statusLabel.getStyleClass().add("section-title");

        VBox submissionCard = new VBox(6, statusLabel, submissionMessage);
        submissionCard.getStyleClass().add("surface");
        submissionCard.setFillWidth(true);

        startButton.setOnAction(
                event -> startLoadTest()
        );

        VBox configurationContent = new VBox(
                18,
                title,
                subtitle,
                connectionStatusLabel,
                form,
                startButton,
                submissionCard
        );

        configurationContent.setPadding(
                new Insets(20)
        );

        configurationContent.setAlignment(
                Pos.TOP_LEFT
        );

        ScrollPane configurationScroll = new ScrollPane(configurationContent);
        configurationScroll.setFitToWidth(true);
        configurationTab = new Tab("Configuration", configurationScroll);

        executionTab = new Tab(
                "Execution Monitor"
        );

        configurationTab.setClosable(false);
        executionTab.setClosable(false);

        internalTabs = new TabPane(
                configurationTab,
                executionTab
        );

        executionTab.setDisable(true);

        getChildren().add(internalTabs);
        VBox.setVgrow(internalTabs, Priority.ALWAYS);

        setAlignment(Pos.TOP_LEFT);

        /*
         * Monitor tunnel connection every second.
         */
        connectionMonitor = new Timeline(
                new KeyFrame(
                        javafx.util.Duration.seconds(1),
                        event -> updateConnectionStatus()
                )
        );

        connectionMonitor.setCycleCount(
                Timeline.INDEFINITE
        );

        updateConnectionStatus();
        connectionMonitor.play();
    }

    private boolean isTunnelConnected() {

        return tunnelClient.getStatus() != null
                && "CONNECTED".equalsIgnoreCase(
                tunnelClient.getStatus().toString()
        );
    }

    private void updateConnectionStatus() {

        boolean connected = isTunnelConnected();

        if (connected) {

            connectionStatusLabel.setText(
                    "Tunnel Status: CONNECTED"
            );

            connectionStatusLabel.setStyle(
                    "-fx-text-fill: #6ee7aa; "
                            + "-fx-font-weight: bold;"
            );

        } else {

            connectionStatusLabel.setText(
                    "Tunnel Status: DISCONNECTED"
            );

            connectionStatusLabel.setStyle(
                    "-fx-text-fill: #fda4af; "
                            + "-fx-font-weight: bold;"
            );
        }

        startButton.setDisable(
                !connected || submitting
        );
    }

    private void startLoadTest() {

        updateConnectionStatus();

        if (!isTunnelConnected()) {

            statusLabel.setText(
                    "Cannot start load test"
            );

            submissionMessage.setText(
                    "Your Local Agent is not connected "
                            + "to the cloud gateway.\n"
                            + "Open the Connection tab and "
                            + "establish the tunnel first."
            );

            return;
        }

        try {

            String targetUrl =
                    targetUrlField.getText().trim();

            if (targetUrl.isBlank()) {
                throw new IllegalArgumentException(
                        "Target URL cannot be empty"
                );
            }

            int totalRequests = Integer.parseInt(
                    totalRequestsField.getText().trim()
            );

            int concurrency = Integer.parseInt(
                    concurrencyField.getText().trim()
            );

            int timeoutMillis = Integer.parseInt(
                    timeoutField.getText().trim()
            );

            int expectedStatusCode = Integer.parseInt(
                    expectedStatusField.getText().trim()
            );

            if (totalRequests < 1 || totalRequests > 100000) {
                throw new IllegalArgumentException(
                        "Requests must be between 1 and 100000"
                );
            }

            if (concurrency < 1
                    || concurrency > 1000
                    || concurrency > totalRequests) {

                throw new IllegalArgumentException(
                        "Concurrency must be between 1 and "
                                + "the total number of requests"
                );
            }

            if (timeoutMillis < 1 || timeoutMillis > 60000) {
                throw new IllegalArgumentException(
                        "Timeout must be between 1 and 60000 ms"
                );
            }

            if (expectedStatusCode < 100
                    || expectedStatusCode > 599) {

                throw new IllegalArgumentException(
                        "Enter a valid HTTP status code"
                );
            }

            LoadTestConfiguration configuration =
                    new LoadTestConfiguration();

            configuration.setTargetUrl(targetUrl);
            configuration.setMethod(methodField.getValue());
            configuration.setHeaders(parseEntries(
                    headersField.getText(), ':', "Header", new TreeMap<>(String.CASE_INSENSITIVE_ORDER)
            ));
            configuration.setQueryParams(parseEntries(
                    queryParamsField.getText(), '=', "Query parameter", new LinkedHashMap<>()
            ));
            if (!"GET".equals(configuration.getMethod()) && !bodyField.getText().isEmpty()) {
                configuration.setRequestBody(bodyField.getText());
            }
            configuration.setTotalRequests(totalRequests);
            configuration.setConcurrency(concurrency);
            configuration.setTimeoutMillis(timeoutMillis);

            String expectedResponseBody = expectedResponseBodyField.getText();
            if (!expectedResponseBody.isEmpty()) {
                configuration.setExpectedBodyContains(expectedResponseBody);
            }

            configuration.setExpectedStatusCode(
                    expectedStatusCode
            );

            /*
             * Store individual request results only
             * when the user enables this option.
             */
            configuration.setIncludeRequestDetails(
                    includeRequestDetailsCheckBox.isSelected()
            );

            // Load generators always run in Testmyload cloud.
            configuration.setGatewayHost(gatewayHost);
            configuration.setGatewayPort(9100);

            submitting = true;
            updateConnectionStatus();

            statusLabel.setText(
                    "Submitting load test..."
            );

            submissionMessage.setText("");

            apiClient.startLoadTest(configuration)
                    .whenComplete((response, error) ->
                            Platform.runLater(() -> {

                                submitting = false;
                                updateConnectionStatus();

                                if (error != null) {

                                    statusLabel.setText(
                                            "Failed to start load test"
                                    );

                                    submissionMessage.setText(
                                            getErrorMessage(error)
                                    );

                                    return;
                                }

                                handleSubmissionResponse(
                                        response
                                );
                            })
                    );

        } catch (NumberFormatException e) {

            statusLabel.setText(
                    "Invalid configuration"
            );

            submissionMessage.setText(
                    "Enter valid numeric values "
                            + "in all numeric fields."
            );

        } catch (IllegalArgumentException e) {

            statusLabel.setText(
                    "Invalid configuration"
            );

            submissionMessage.setText(
                    e.getMessage()
            );
        }
    }

    private static Map<String, String> parseEntries(
            String text, char separator, String label, Map<String, String> entries
    ) {
        String[] lines = text.split("\\R");
        for (int index = 0; index < lines.length; index++) {
            String line = lines[index];
            if (line.isBlank()) {
                continue;
            }

            int split = line.indexOf(separator);
            String name = split < 0 ? "" : line.substring(0, split).trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException(
                        label + " on line " + (index + 1) + " must use name" + separator + "value"
                );
            }

            String value = line.substring(split + 1);
            if (separator == ':') {
                value = value.trim();
            }
            if (entries.putIfAbsent(name, value) != null) {
                throw new IllegalArgumentException(label + " on line " + (index + 1) + " repeats a name");
            }
        }
        return entries;
    }

    private void handleSubmissionResponse(
            JsonNode response
    ) {

        String testId = response
                .path("testId")
                .asText("");

        if (testId.isBlank()) {

            statusLabel.setText(
                    "Unexpected backend response"
            );

            submissionMessage.setText(
                    "The gateway did not return a test ID."
            );

            return;
        }

        statusLabel.setText(
                "Load test submitted successfully"
        );

        submissionMessage.setText(
                "Test ID: " + testId
        );

        /*
         * Stop monitoring the previous execution view.
         */
        if (currentExecutionView != null) {
            currentExecutionView.stopMonitoring();
        }

        /*
         * Create a new execution monitor.
         */
        currentExecutionView = new TestExecutionView(
                apiClient,
                testId
        );

        executionTab.setContent(
                currentExecutionView
        );

        executionTab.setDisable(false);

        /*
         * Automatically open the execution monitor.
         */
        internalTabs.getSelectionModel().select(
                executionTab
        );
    }

    private String getErrorMessage(Throwable error) {

        Throwable cause = error;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause.getMessage() != null
                ? cause.getMessage()
                : "Unknown API error";
    }

    public void stopMonitoring() {

        connectionMonitor.stop();

        if (currentExecutionView != null) {
            currentExecutionView.stopMonitoring();
        }
    }
}
