
package com.software.localagent.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.software.localagent.api.LoadTestApiClient;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TestExecutionView extends VBox {

    private static final PseudoClass PASSED_POINT = PseudoClass.getPseudoClass("passed");
    private static final PseudoClass FAILED_POINT = PseudoClass.getPseudoClass("failed");
    private static final PseudoClass SELECTED_POINT = PseudoClass.getPseudoClass("selected");
    private static final String THROUGHPUT_RATE_LABEL = "Finished attempts/sec";
    private static final int REQUEST_PAGE_SIZE = 100;
    private static final int CSV_PAGE_SIZE = 1000;

    private final LoadTestApiClient apiClient;
    private final String testId;

    private final Label statusLabel;
    private final Label totalRequestsLabel;
    private final Label passedLabel;
    private final Label failedLabel;
    private final Label errorLabel;
    private final Label throughputLabel;
    private final Label averageLabel;
    private final Label minLabel;
    private final Label maxLabel;
    private final Label p50Label;
    private final Label p95Label;
    private final Label p99Label;

    private final Label requestPageLabel;

    private final Label activityMessage;
    private final Label runSummary;
    private final Label failureSummary;

    private final PieChart requestOutcomeChart;
    private final LineChart<Number, Number> throughputChart;
    private final ComboBox<String> throughputMode;
    private final Label throughputSummary;
    private final BarChart<String, Number> responseTimeChart;
    private final ScatterChart<Number, Number> requestScatterChart;
    private final Label requestScatterSelection;
    private final Label requestScatterDetails;
    private Node selectedRequestPoint;

    private final TableView<RequestItem> requestTable;
    private final ObservableList<RequestItem> requestItems;

    private final Timeline statusMonitor;

    private final Button exportCsvButton;
    private final Button previousPageButton;
    private final Button nextPageButton;

    private boolean fetchingResults = false;
    private boolean requestPageLoading = false;

    private int currentRequestPage = 0;
    private int totalRequestPages = 0;
    private long totalRequestDetails = 0;

    private JsonNode latestResults;

    public TestExecutionView(
            LoadTestApiClient apiClient,
            String testId
    ) {

        if (apiClient == null) {
            throw new IllegalArgumentException(
                    "API client cannot be null"
            );
        }

        if (testId == null || testId.isBlank()) {
            throw new IllegalArgumentException(
                    "Test ID cannot be empty"
            );
        }

        this.apiClient = apiClient;
        this.testId = testId;

        setSpacing(18);
        setPadding(new Insets(20));

        Label title = new Label("Test Execution Monitor");

        title.setStyle("""
                -fx-font-size: 24px;
                -fx-font-weight: bold;
                """);

        Label testIdLabel = new Label("Test ID: " + testId);

        statusLabel = new Label("Status: Checking...");
        statusLabel.setStyle("-fx-font-weight: bold;");

        totalRequestsLabel = new Label("Total Requests: -");
        passedLabel = new Label("Passed: -");
        failedLabel = new Label("Failed: -");
        errorLabel = new Label("Errors: -");

        throughputLabel = new Label("Throughput: -");
        averageLabel = new Label("Average Response Time: -");

        minLabel = new Label("Minimum Response Time: -");
        maxLabel = new Label("Maximum Response Time: -");

        p50Label = new Label("P50: -");
        p95Label = new Label("P95: -");
        p99Label = new Label("P99: -");

        GridPane metricsGrid = new GridPane();

        metricsGrid.setHgap(12);
        metricsGrid.setVgap(12);

        metricsGrid.addRow(0, totalRequestsLabel, passedLabel, failedLabel);
        metricsGrid.addRow(1, errorLabel, throughputLabel, averageLabel);
        metricsGrid.addRow(2, minLabel, maxLabel, p50Label);
        metricsGrid.addRow(3, p95Label, p99Label);

        metricsGrid.getChildren().forEach(metric -> {
            metric.getStyleClass().add("metric");
            GridPane.setHgrow(metric, Priority.ALWAYS);
            ((Label) metric).setMaxWidth(Double.MAX_VALUE);
        });

        // -----------------------------------------------------
        // REQUEST OUTCOME CHART
        // -----------------------------------------------------

        requestOutcomeChart = new PieChart();

        requestOutcomeChart.setTitle("Request Outcomes");
        requestOutcomeChart.setLegendVisible(true);
        requestOutcomeChart.setLabelsVisible(true);
        requestOutcomeChart.setPrefHeight(300);

        // -----------------------------------------------------
        // THROUGHPUT CHART
        // -----------------------------------------------------

        NumberAxis throughputXAxis = new NumberAxis();
        throughputXAxis.setLabel("Elapsed Time (seconds)");

        NumberAxis throughputYAxis = new NumberAxis();
        throughputYAxis.setLabel(THROUGHPUT_RATE_LABEL);

        throughputChart = new LineChart<>(
                throughputXAxis,
                throughputYAxis
        );

        throughputMode = new ComboBox<>();
        throughputMode.getItems().addAll("Rate", "Cumulative");
        throughputMode.setValue("Rate");
        throughputMode.setDisable(true);
        throughputMode.setAccessibleText("Throughput chart view");
        throughputMode.setOnAction(event -> {
            if (latestResults != null) {
                updateThroughputChart(latestResults);
            }
        });

        throughputSummary = new Label();
        throughputSummary.setWrapText(true);
        throughputSummary.getStyleClass().add("muted");
        throughputSummary.managedProperty().bind(throughputSummary.textProperty().isNotEmpty());

        Label throughputHeading = new Label("Throughput Over Time");
        throughputHeading.getStyleClass().add("section-title");
        Region headingSpacer = new Region();
        HBox.setHgrow(headingSpacer, Priority.ALWAYS);
        HBox throughputHeader = new HBox(12, throughputHeading, headingSpacer, throughputMode);
        throughputHeader.setAlignment(Pos.CENTER_LEFT);

        VBox throughputCard = new VBox(10, throughputHeader, throughputSummary, throughputChart);
        throughputCard.getStyleClass().add("surface");
        throughputChart.getStyleClass().add("embedded-chart");
        throughputChart.setAnimated(false);
        throughputChart.setLegendVisible(false);
        throughputChart.setPrefHeight(350);

        // -----------------------------------------------------
        // RESPONSE-TIME DISTRIBUTION CHART
        // -----------------------------------------------------

        CategoryAxis responseXAxis = new CategoryAxis();
        responseXAxis.setLabel("Response Time");

        NumberAxis responseYAxis = new NumberAxis();
        responseYAxis.setLabel("Number of Requests");

        responseTimeChart = new BarChart<>(
                responseXAxis,
                responseYAxis
        );

        responseTimeChart.setTitle("Response Time Distribution");
        responseTimeChart.setAnimated(false);
        responseTimeChart.setLegendVisible(false);
        responseTimeChart.setPrefHeight(350);

        // -----------------------------------------------------
        // INDIVIDUAL REQUEST RESULTS TABLE
        // -----------------------------------------------------

        NumberAxis requestXAxis = new NumberAxis();
        requestXAxis.setLabel("Request Number");
        requestXAxis.setForceZeroInRange(false);

        NumberAxis requestYAxis = new NumberAxis();
        requestYAxis.setLabel("Response Time (ms)");

        requestScatterChart = new ScatterChart<>(requestXAxis, requestYAxis);
        requestScatterChart.setAnimated(false);
        requestScatterChart.setLegendVisible(false);
        requestScatterChart.setPrefHeight(350);
        requestScatterChart.getStyleClass().add("embedded-chart");

        Label requestScatterHeading = new Label("Individual Request Response Times");
        requestScatterHeading.getStyleClass().add("section-title");
        requestScatterSelection = new Label();
        requestScatterSelection.getStyleClass().add("request-detail-title");
        requestScatterDetails = new Label();
        requestScatterDetails.setWrapText(true);
        requestScatterDetails.getStyleClass().add("muted");
        requestScatterDetails.managedProperty().bind(requestScatterDetails.textProperty().isNotEmpty());

        VBox requestDetailPanel = new VBox(4, requestScatterSelection, requestScatterDetails);
        requestDetailPanel.getStyleClass().add("request-detail");
        VBox requestScatterCard = new VBox(
                12, requestScatterHeading, requestScatterChart, requestDetailPanel
        );
        requestScatterCard.getStyleClass().add("surface");
        requestScatterCard.visibleProperty().bind(
                Bindings.isNotEmpty(requestScatterChart.getData())
        );
        requestScatterCard.managedProperty().bind(requestScatterCard.visibleProperty());

        requestTable = new TableView<>();

        requestItems = FXCollections.observableArrayList();
        requestTable.setItems(requestItems);

        TableColumn<RequestItem, String> numberColumn =
                new TableColumn<>("Request #");

        numberColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getRequestNumber()
                )
        );

        TableColumn<RequestItem, String> resultColumn =
                new TableColumn<>("Result");

        resultColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getResult()
                )
        );

        TableColumn<RequestItem, String> statusColumn =
                new TableColumn<>("HTTP Status");

        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getStatusCode()
                )
        );

        TableColumn<RequestItem, String> responseTimeColumn =
                new TableColumn<>("Response Time");

        responseTimeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getResponseTime()
                )
        );

        TableColumn<RequestItem, String> failureColumn =
                new TableColumn<>("Failure Type");

        failureColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getFailureType()
                )
        );

        TableColumn<RequestItem, String> errorColumn =
                new TableColumn<>("Error");

        errorColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getError()
                )
        );

        numberColumn.getStyleClass().add("numeric-column");
        statusColumn.getStyleClass().add("numeric-column");
        responseTimeColumn.getStyleClass().add("numeric-column");

        numberColumn.setPrefWidth(100);
        resultColumn.setPrefWidth(100);
        statusColumn.setPrefWidth(100);
        responseTimeColumn.setPrefWidth(130);
        failureColumn.setPrefWidth(160);
        errorColumn.setPrefWidth(300);

        requestTable.getColumns().addAll(
                numberColumn,
                resultColumn,
                statusColumn,
                responseTimeColumn,
                failureColumn,
                errorColumn
        );

        requestTable.setPrefHeight(350);

        // -----------------------------------------------------
        // REQUEST TABLE PAGINATION CONTROLS
        // -----------------------------------------------------

        previousPageButton = new Button("Previous");
        nextPageButton = new Button("Next");

        requestPageLabel = new Label(
                "Page 0 of 0 | 0 records"
        );

        previousPageButton.setDisable(true);
        nextPageButton.setDisable(true);

        previousPageButton.setOnAction(event -> {

            if (currentRequestPage > 0) {
                loadRequestPage(currentRequestPage - 1);
            }
        });

        nextPageButton.setOnAction(event -> {

            if (currentRequestPage + 1 < totalRequestPages) {
                loadRequestPage(currentRequestPage + 1);
            }
        });

        HBox paginationControls = new HBox(
                12,
                previousPageButton,
                requestPageLabel,
                nextPageButton
        );

        paginationControls.setAlignment(Pos.CENTER_LEFT);
        requestPageLabel.setWrapText(true);
        requestPageLabel.getStyleClass().add("muted");

        activityMessage = new Label();
        activityMessage.setWrapText(true);
        activityMessage.getStyleClass().add("muted");
        activityMessage.managedProperty().bind(activityMessage.textProperty().isNotEmpty());

        runSummary = new Label();
        runSummary.setWrapText(true);
        runSummary.getStyleClass().add("section-title");
        runSummary.managedProperty().bind(runSummary.textProperty().isNotEmpty());

        failureSummary = new Label();
        failureSummary.setWrapText(true);
        failureSummary.getStyleClass().add("muted");
        failureSummary.managedProperty().bind(failureSummary.textProperty().isNotEmpty());

        VBox overview = new VBox(8, statusLabel, runSummary, failureSummary, activityMessage);
        overview.getStyleClass().add("surface");

        HBox outcomeCharts = new HBox(16, requestOutcomeChart, responseTimeChart);
        HBox.setHgrow(requestOutcomeChart, Priority.ALWAYS);
        HBox.setHgrow(responseTimeChart, Priority.ALWAYS);
        requestOutcomeChart.setMinWidth(0);
        responseTimeChart.setMinWidth(0);

        // -----------------------------------------------------
        // CSV EXPORT
        // -----------------------------------------------------

        exportCsvButton = new Button("Export CSV");
        exportCsvButton.setDisable(true);

        exportCsvButton.setOnAction(
                event -> exportResultsToCsv()
        );

        HBox exportControls = new HBox(
                10,
                exportCsvButton
        );

        // -----------------------------------------------------
        // MAIN CONTENT
        // -----------------------------------------------------

        Label metricsHeading = new Label("Performance Metrics");
        metricsHeading.getStyleClass().add("section-title");
        Label requestsHeading = new Label("Individual Request Results");
        requestsHeading.getStyleClass().add("section-title");

        VBox content = new VBox(
                20,
                title,
                testIdLabel,
                overview,
                metricsHeading,
                metricsGrid,
                exportControls,
                throughputCard,
                outcomeCharts,
                requestsHeading,
                paginationControls,
                requestScatterCard,
                requestTable
        );

        content.setPadding(new Insets(15));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFocusTraversable(true);

        getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // -----------------------------------------------------
        // BACKEND STATUS POLLING
        // -----------------------------------------------------

        statusMonitor = new Timeline(
                new KeyFrame(
                        Duration.seconds(1),
                        event -> checkTestStatus()
                )
        );

        statusMonitor.setCycleCount(Timeline.INDEFINITE);

        checkTestStatus();
        statusMonitor.play();
    }

    // ---------------------------------------------------------
    // CHECK TEST STATUS
    // ---------------------------------------------------------

    private void checkTestStatus() {

        apiClient.getTestStatus(testId)
                .whenComplete((response, error) -> {

                    if (error != null) {

                        Platform.runLater(() ->
                                activityMessage.setText(
                                        "Unable to retrieve test status:\n"
                                                + getErrorMessage(error)
                                )
                        );

                        return;
                    }

                    Platform.runLater(() ->
                            updateStatus(response)
                    );
                });
    }

    // ---------------------------------------------------------
    // UPDATE STATUS
    // ---------------------------------------------------------

    private void updateStatus(JsonNode response) {

        String status = response
                .path("status")
                .asText("UNKNOWN");

        statusLabel.setText("Status: " + status);

        switch (status.toUpperCase()) {

            case "QUEUED" -> {

                statusLabel.setStyle(
                        "-fx-text-fill: #fcd38a; "
                                + "-fx-font-weight: bold;"
                );

                activityMessage.setText(
                        "Your load test is waiting in the execution queue."
                );
            }

            case "RUNNING" -> {

                statusLabel.setStyle(
                        "-fx-text-fill: #93c5fd; "
                                + "-fx-font-weight: bold;"
                );

                activityMessage.setText(
                        "Your load test is currently executing."
                );
            }

            case "COMPLETED" -> {

                statusMonitor.stop();

                statusLabel.setStyle(
                        "-fx-text-fill: #6ee7aa; "
                                + "-fx-font-weight: bold;"
                );

                if (!fetchingResults) {
                    fetchingResults = true;
                    fetchResults();
                }
            }

            case "FAILED" -> {

                statusMonitor.stop();

                statusLabel.setStyle(
                        "-fx-text-fill: #fda4af; "
                                + "-fx-font-weight: bold;"
                );

                activityMessage.setText(
                        "Load test failed:\n"
                                + response.path("error").asText(
                                "Unknown execution error"
                        )
                );
            }

            default -> activityMessage.setText(
                    "Received an unknown test status."
            );
        }
    }

    // ---------------------------------------------------------
    // FETCH AGGREGATE RESULTS
    // ---------------------------------------------------------

    private void fetchResults() {

        activityMessage.setText(
                "Test completed. Retrieving analytics..."
        );

        apiClient.getTestResults(testId)
                .whenComplete((result, error) -> {

                    if (error != null) {

                        Platform.runLater(() -> {

                            fetchingResults = false;

                            activityMessage.setText(
                                    "Test completed, but results "
                                            + "could not be retrieved:\n"
                                            + getErrorMessage(error)
                            );
                        });

                        return;
                    }

                    Platform.runLater(() ->
                            displayResults(result)
                    );
                });
    }

    // ---------------------------------------------------------
    // DISPLAY AGGREGATE RESULTS
    // ---------------------------------------------------------

    private void displayResults(JsonNode result) {

        latestResults = result;
        exportCsvButton.setDisable(false);

        int total = result.path("totalRequests").asInt();
        int passed = result.path("passedRequests").asInt();
        int failed = result.path("failedRequests").asInt();
        int errors = result.path("errorRequests").asInt();

        totalRequestsLabel.setText(
                "Total Requests: " + total
        );

        passedLabel.setText("Passed: " + passed);
        failedLabel.setText("Failed: " + failed);
        errorLabel.setText("Errors: " + errors);

        throughputLabel.setText(
                String.format(
                        "Throughput: %,.2f attempts/sec",
                        result.path("requestsPerSecond").asDouble()
                )
        );

        averageLabel.setText(
                "Average Response Time: "
                        + result.path(
                        "averageResponseTimeMillis"
                ).asDouble()
                        + " ms"
        );

        minLabel.setText(
                "Minimum Response Time: "
                        + result.path("minResponseTimeMillis").asLong()
                        + " ms"
        );

        maxLabel.setText(
                "Maximum Response Time: "
                        + result.path("maxResponseTimeMillis").asLong()
                        + " ms"
        );

        p50Label.setText(
                "P50: "
                        + result.path("p50ResponseTimeMillis").asLong()
                        + " ms"
        );

        p95Label.setText(
                "P95: "
                        + result.path("p95ResponseTimeMillis").asLong()
                        + " ms"
        );

        p99Label.setText(
                "P99: "
                        + result.path("p99ResponseTimeMillis").asLong()
                        + " ms"
        );

        updateOutcomeChart(passed, failed, errors);

        updateThroughputChart(result);

        updateResponseTimeChart(
                result.path("responseTimeDistribution")
        );

        double passRate = total == 0 ? 0 : 100.0 * passed / total;
        runSummary.setText(String.format(
                "%,d of %,d requests passed (%.1f%%) · Test duration %.2f s",
                passed, total, passRate, result.path("totalDurationMillis").asLong() / 1000.0
        ));

        List<String> failureCounts = new ArrayList<>();
        result.path("failureBreakdown").fields().forEachRemaining(entry -> {
            String reason = switch (entry.getKey()) {
                case "HTTP_STATUS" -> "Unexpected HTTP status";
                case "TIMEOUT" -> "Timed out";
                case "CONNECTION_ERROR" -> "Connection error";
                case "RESPONSE_BODY_MISMATCH" -> "Expected response text missing";
                case "RESPONSE_VALIDATION" -> "Response validation failed";
                default -> "Other failures";
            };
            failureCounts.add(reason + " (" + entry.getValue().asInt() + ")");
        });
        failureSummary.setText(String.join("  •  ", failureCounts));
        activityMessage.setText("");

        // Individual request details are fetched separately.
        loadRequestPage(0);
    }

    // ---------------------------------------------------------
    // LOAD PAGINATED REQUEST RESULTS
    // ---------------------------------------------------------

    private void loadRequestPage(int page) {

        if (requestPageLoading || page < 0) {
            return;
        }

        requestPageLoading = true;

        previousPageButton.setDisable(true);
        nextPageButton.setDisable(true);

        requestPageLabel.setText("Loading request records...");

        apiClient.getRequestResults(
                testId,
                page,
                REQUEST_PAGE_SIZE
        ).whenComplete((response, error) -> {

            if (error != null) {

                Platform.runLater(() -> {

                    requestPageLoading = false;

                    updatePaginationButtons();

                    requestPageLabel.setText(
                            "Unable to load request records"
                    );

                    activityMessage.setText(
                            "Unable to retrieve individual request "
                                    + "results:\n"
                                    + getErrorMessage(error)
                    );
                });

                return;
            }

            Platform.runLater(() ->
                    displayRequestPage(response)
            );
        });
    }

    private void selectRequestPoint(Node point, int requestNumber, String details) {
        if (selectedRequestPoint != null) {
            selectedRequestPoint.pseudoClassStateChanged(SELECTED_POINT, false);
        }
        selectedRequestPoint = point;
        point.pseudoClassStateChanged(SELECTED_POINT, true);
        requestScatterSelection.setText(String.format("Request #%,d", requestNumber));
        requestScatterDetails.setText(details);
    }

    private void updateRequestScatterChart(JsonNode requests) {
        requestScatterChart.getData().clear();
        selectedRequestPoint = null;
        requestScatterSelection.setText("Select a dot to inspect a request");
        requestScatterDetails.setText("");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (JsonNode request : requests) {
            JsonNode number = request.path("requestNumber");
            JsonNode time = request.path("responseTimeMillis");
            if (!number.isIntegralNumber() || !time.isIntegralNumber()) {
                continue;
            }

            int requestNumber = number.asInt();
            long responseTime = time.asLong();
            int status = request.path("statusCode").asInt(-1);
            String httpStatus = status > 0 ? "HTTP " + status : "No HTTP status recorded";
            String failure = request.path("error").asText("").strip();
            if (failure.isEmpty()) {
                failure = request.path("failureType").asText("");
            }
            boolean passed = request.path("passed").asBoolean();
            String details = String.format("%s  ·  Response time  %,d ms  ·  %s",
                    passed ? "Passed" : "Failed", responseTime, httpStatus);
            String failureDetails = !failure.isBlank() && !"NONE".equals(failure)
                    ? details + "\nFailure reason  " + failure
                    : details;
            XYChart.Data<Number, Number> point = new XYChart.Data<>(requestNumber, responseTime);
            StackPane symbol = new StackPane();
            symbol.pseudoClassStateChanged(passed ? PASSED_POINT : FAILED_POINT, true);
            symbol.setFocusTraversable(true);
            symbol.setAccessibleText(String.format("Request #%d, %s", requestNumber, failureDetails));
            symbol.setOnMouseClicked(event -> selectRequestPoint(symbol, requestNumber, failureDetails));
            symbol.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER, SPACE -> {
                        selectRequestPoint(symbol, requestNumber, failureDetails);
                        event.consume();
                    }
                    default -> { }
                }
            });
            point.setNode(symbol);
            series.getData().add(point);
        }

        if (!series.getData().isEmpty()) {
            requestScatterChart.getData().add(series);
        }
    }

    // ---------------------------------------------------------
    // DISPLAY REQUEST PAGE
    // ---------------------------------------------------------

    private void displayRequestPage(JsonNode response) {

        requestPageLoading = false;

        currentRequestPage = response
                .path("page")
                .asInt(0);

        totalRequestPages = response
                .path("totalPages")
                .asInt(0);

        totalRequestDetails = response
                .path("totalElements")
                .asLong(0);

        requestItems.clear();

        JsonNode content = response.path("content");
        updateRequestScatterChart(content);

        if (content.isArray()) {

            for (JsonNode request : content) {

                String result = request.path("passed").asBoolean()
                        ? "PASSED"
                        : "FAILED";

                String requestNumber = request
                        .path("requestNumber")
                        .asText("-");

                RequestItem item = new RequestItem(
                        requestNumber,
                        result,
                        request.path("statusCode").asText("-"),
                        request.path("responseTimeMillis").asText("-")
                                + " ms",
                        "RESPONSE_BODY_MISMATCH".equals(request.path("failureType").asText())
                                ? "Body mismatch"
                                : request.path("failureType").asText("NONE"),
                        request.path("error").asText("-")
                );

                requestItems.add(item);
            }
        }

        if (totalRequestDetails == 0) {

            requestPageLabel.setText(
                    "No individual request records stored"
            );

            activityMessage.setText(
                    "Individual request details are not available.\n"
                            + "Enable Include Request Details when "
                            + "creating a new load test to store them."
            );

        } else {

            String pageDetails = String.format("Page %,d of %,d  ·  %,d records",
                    currentRequestPage + 1, totalRequestPages, totalRequestDetails);
            if (content.isArray() && !content.isEmpty()) {
                int first = content.get(0).path("requestNumber").asInt();
                int last = content.get(content.size() - 1).path("requestNumber").asInt();
                if (first > 0 && last >= first) {
                    pageDetails += String.format("  ·  Requests #%,d–#%,d", first, last);
                }
            }
            requestPageLabel.setText(pageDetails);
        }

        updatePaginationButtons();
    }

    // ---------------------------------------------------------
    // UPDATE PAGINATION BUTTONS
    // ---------------------------------------------------------

    private void updatePaginationButtons() {

        previousPageButton.setDisable(
                requestPageLoading
                        || currentRequestPage <= 0
                        || totalRequestPages == 0
        );

        nextPageButton.setDisable(
                requestPageLoading
                        || currentRequestPage >= totalRequestPages - 1
                        || totalRequestPages == 0
        );
    }

    // ---------------------------------------------------------
    // REQUEST OUTCOME CHART
    // ---------------------------------------------------------

    private void updateOutcomeChart(
            int passed,
            int failed,
            int errors
    ) {

        requestOutcomeChart.getData().clear();

        List<String> colors = new ArrayList<>(3);
        addPieSlice("Passed", passed, "#54d49b", colors);
        addPieSlice("Failed", failed - errors, "#fb7185", colors);
        addPieSlice("Errors", errors, "#f4839a", colors);

        StringBuilder palette = new StringBuilder();
        for (int index = 0; index < colors.size(); index++) {
            palette.append("CHART_COLOR_").append(index + 1).append(": ")
                    .append(colors.get(index)).append(';');
        }
        requestOutcomeChart.setStyle(palette.toString());
    }

    private void addPieSlice(String name, int value, String color, List<String> colors) {

        if (value > 0) {

            requestOutcomeChart.getData().add(
                    new PieChart.Data(name, value)
            );
            colors.add(color);
        }
    }

    // ---------------------------------------------------------
    // THROUGHPUT CHART
    // ---------------------------------------------------------

    private void updateThroughputChart(JsonNode result) {
        throughputChart.getData().clear();
        throughputSummary.setText("");

        JsonNode timeline = result.path("throughputTimeline");
        boolean hasPoints = timeline.isArray() && !timeline.isEmpty();
        throughputMode.setDisable(!hasPoints);
        if (!hasPoints) {
            throughputSummary.setText("Throughput data is unavailable for this run.");
            return;
        }

        boolean cumulative = "Cumulative".equals(throughputMode.getValue());
        throughputChart.getYAxis().setLabel(cumulative ? "Finished Attempts" : THROUGHPUT_RATE_LABEL);

        double duration = result.path("totalDurationMillis").asLong() / 1000.0;
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(cumulative ? "Finished Attempts" : THROUGHPUT_RATE_LABEL);
        if (cumulative) {
            series.getData().add(new XYChart.Data<>(0, 0));
        }

        int runningTotal = 0;

        for (JsonNode point : timeline) {
            double bucketEnd = point.path("elapsedSeconds").asDouble();
            double bucketStart = Math.max(0, bucketEnd - 1);
            double elapsed = duration > bucketStart ? Math.min(bucketEnd, duration) : bucketEnd;
            int completed = point.path("completedRequests").asInt();
            double rate = point.path("requestsPerSecond").asDouble();
            runningTotal += completed;

            XYChart.Data<Number, Number> data = new XYChart.Data<>(
                    elapsed, cumulative ? runningTotal : rate
            );
            String details = String.format(
                    "%,d finished in %.3f s · %,.1f attempts/sec",
                    completed, elapsed - bucketStart, rate
            );
            if (cumulative) {
                details += String.format("%n%,d finished in total", runningTotal);
            }
            Tooltip tooltip = new Tooltip(details);
            data.nodeProperty().addListener((property, previous, node) -> {
                if (previous != null) {
                    Tooltip.uninstall(previous, tooltip);
                }
                if (node != null) {
                    Tooltip.install(node, tooltip);
                }
            });
            if (timeline.size() == 1) {
                throughputSummary.setText(details.lines().findFirst().orElse(""));
            }
            series.getData().add(data);
        }


        throughputChart.getData().add(series);
    }

    // ---------------------------------------------------------
    // RESPONSE-TIME DISTRIBUTION CHART
    // ---------------------------------------------------------

    private void updateResponseTimeChart(JsonNode distribution) {

        responseTimeChart.getData().clear();

        if (!distribution.isObject()) {
            return;
        }

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        distribution.fields().forEachRemaining(entry -> {

            series.getData().add(
                    new XYChart.Data<>(
                            entry.getKey(),
                            entry.getValue().asInt()
                    )
            );
        });

        responseTimeChart.getData().add(series);
    }

    // ---------------------------------------------------------
    // CSV EXPORT
    // ---------------------------------------------------------

    private void exportResultsToCsv() {

        if (latestResults == null) {

            showExportMessage(
                    Alert.AlertType.WARNING,
                    "No results available to export."
            );

            return;
        }

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Export Load Test Results");

        fileChooser.setInitialFileName(
                "testmyload_" + testId + ".csv"
        );

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "CSV Files",
                        "*.csv"
                )
        );

        Window window = getScene() == null
                ? null
                : getScene().getWindow();

        File file = fileChooser.showSaveDialog(window);

        if (file == null) {
            return;
        }

        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        File exportFile = file;

        exportCsvButton.setDisable(true);

        activityMessage.setText(
                "Preparing CSV export. Fetching all request records..."
        );

        fetchAllRequestResults(
                0,
                new ArrayList<>()
        ).whenComplete((allRequests, error) -> {

            if (error != null) {

                Platform.runLater(() -> {

                    exportCsvButton.setDisable(false);

                    showExportMessage(
                            Alert.AlertType.ERROR,
                            "Unable to retrieve all request records:\n"
                                    + getErrorMessage(error)
                    );
                });

                return;
            }

            try {

                writeCsvFile(exportFile, allRequests);

                Platform.runLater(() -> {

                    exportCsvButton.setDisable(false);

                    showExportMessage(
                            Alert.AlertType.INFORMATION,
                            "CSV exported successfully:\n"
                                    + exportFile.getAbsolutePath()
                    );
                });

            } catch (IOException e) {

                Platform.runLater(() -> {

                    exportCsvButton.setDisable(false);

                    showExportMessage(
                            Alert.AlertType.ERROR,
                            "CSV export failed:\n" + e.getMessage()
                    );
                });
            }
        });
    }

    // ---------------------------------------------------------
    // FETCH ALL REQUEST PAGES FOR CSV
    // ---------------------------------------------------------

    private CompletableFuture<List<JsonNode>> fetchAllRequestResults(
            int page,
            List<JsonNode> collected
    ) {

        return apiClient.getRequestResults(
                testId,
                page,
                CSV_PAGE_SIZE
        ).thenCompose(response -> {

            JsonNode content = response.path("content");

            if (content.isArray()) {

                for (JsonNode request : content) {
                    collected.add(request);
                }
            }

            boolean lastPage = response
                    .path("last")
                    .asBoolean(true);

            if (lastPage) {
                return CompletableFuture.completedFuture(collected);
            }

            return fetchAllRequestResults(
                    page + 1,
                    collected
            );
        });
    }

    // ---------------------------------------------------------
    // WRITE CSV FILE
    // ---------------------------------------------------------

    private void writeCsvFile(
            File file,
            List<JsonNode> allRequests
    ) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(
                file.toPath(),
                StandardCharsets.UTF_8
        )) {

            writeCsvRow(
                    writer,
                    "Section",
                    "Metric",
                    "Value",
                    "Elapsed Seconds",
                    "Completed Requests",
                    THROUGHPUT_RATE_LABEL,
                    "Request Number",
                    "Passed",
                    "HTTP Status",
                    "Response Time (ms)",
                    "Failure Type",
                    "Error"
            );

            writeSummary(writer);
            writeThroughput(writer);
            writeResponseDistribution(writer);
            writeRequestDetails(writer, allRequests);
        }
    }

    // ---------------------------------------------------------
    // CSV SUMMARY
    // ---------------------------------------------------------

    private void writeSummary(BufferedWriter writer)
            throws IOException {

        String[] metrics = {
                "totalRequests",
                "passedRequests",
                "failedRequests",
                "errorRequests",
                "totalDurationMillis",
                "requestsPerSecond",
                "averageResponseTimeMillis",
                "minResponseTimeMillis",
                "maxResponseTimeMillis",
                "p50ResponseTimeMillis",
                "p95ResponseTimeMillis",
                "p99ResponseTimeMillis"
        };

        writeCsvRow(
                writer,
                "SUMMARY",
                "testId",
                testId
        );

        for (String metric : metrics) {

            JsonNode value = latestResults.path(metric);

            writeCsvRow(
                    writer,
                    "SUMMARY",
                    metric,
                    value.isMissingNode() || value.isNull()
                            ? ""
                            : value.asText()
            );
        }
    }

    // ---------------------------------------------------------
    // CSV THROUGHPUT
    // ---------------------------------------------------------

    private void writeThroughput(BufferedWriter writer)
            throws IOException {

        JsonNode timeline = latestResults.path(
                "throughputTimeline"
        );

        if (!timeline.isArray()) {
            return;
        }

        for (JsonNode point : timeline) {

            writeCsvRow(
                    writer,
                    "THROUGHPUT",
                    "timeline",
                    "",
                    point.path("elapsedSeconds").asText(),
                    point.path("completedRequests").asText(),
                    point.path("requestsPerSecond").asText()
            );
        }
    }

    // ---------------------------------------------------------
    // CSV RESPONSE-TIME DISTRIBUTION
    // ---------------------------------------------------------

    private void writeResponseDistribution(BufferedWriter writer)
            throws IOException {

        JsonNode distribution = latestResults.path(
                "responseTimeDistribution"
        );

        if (!distribution.isObject()) {
            return;
        }

        var fields = distribution.fields();

        while (fields.hasNext()) {

            var entry = fields.next();

            writeCsvRow(
                    writer,
                    "RESPONSE_TIME_DISTRIBUTION",
                    entry.getKey(),
                    entry.getValue().asText()
            );
        }
    }

    // ---------------------------------------------------------
    // CSV INDIVIDUAL REQUEST DETAILS
    // ---------------------------------------------------------

    private void writeRequestDetails(
            BufferedWriter writer,
            List<JsonNode> requests
    ) throws IOException {

        for (JsonNode request : requests) {

            writeCsvRow(
                    writer,
                    "REQUEST",
                    "Individual Request",
                    "",
                    "",
                    "",
                    "",
                    request.path("requestNumber").asText("-"),
                    request.path("passed").asText(),
                    request.path("statusCode").asText(),
                    request.path("responseTimeMillis").asText(),
                    request.path("failureType").asText(),
                    request.path("error").asText()
            );
        }
    }

    // ---------------------------------------------------------
    // CSV ROW WRITER
    // ---------------------------------------------------------

    private void writeCsvRow(
            BufferedWriter writer,
            String... values
    ) throws IOException {

        for (int i = 0; i < values.length; i++) {

            if (i > 0) {
                writer.write(",");
            }

            String value = values[i] == null
                    ? ""
                    : values[i];

            writer.write(
                    "\"" + value.replace("\"", "\"\"") + "\""
            );
        }

        writer.newLine();
    }

    // ---------------------------------------------------------
    // EXPORT MESSAGE
    // ---------------------------------------------------------

    private void showExportMessage(
            Alert.AlertType type,
            String message
    ) {

        Alert alert = new Alert(type);
        if (getScene() != null) {
            alert.initOwner(getScene().getWindow());
            alert.getDialogPane().getStylesheets().setAll(getScene().getStylesheets());
        }
        if (activityMessage.getText().startsWith("Preparing CSV export")) {
            activityMessage.setText("");
        }

        alert.setTitle("CSV Export");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ---------------------------------------------------------
    // ERROR MESSAGE
    // ---------------------------------------------------------

    private String getErrorMessage(Throwable error) {

        Throwable cause = error;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause.getMessage() != null
                ? cause.getMessage()
                : "Unknown API error";
    }

    // ---------------------------------------------------------
    // STOP STATUS MONITORING
    // ---------------------------------------------------------

    public void stopMonitoring() {
        statusMonitor.stop();
    }

    // ---------------------------------------------------------
    // REQUEST TABLE MODEL
    // ---------------------------------------------------------

    private static class RequestItem {

        private final String requestNumber;
        private final String result;
        private final String statusCode;
        private final String responseTime;
        private final String failureType;
        private final String error;

        public RequestItem(
                String requestNumber,
                String result,
                String statusCode,
                String responseTime,
                String failureType,
                String error
        ) {

            this.requestNumber = requestNumber;
            this.result = result;
            this.statusCode = statusCode;
            this.responseTime = responseTime;
            this.failureType = failureType;
            this.error = error;
        }

        public String getRequestNumber() {
            return requestNumber;
        }

        public String getResult() {
            return result;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public String getResponseTime() {
            return responseTime;
        }

        public String getFailureType() {
            return failureType;
        }

        public String getError() {
            return error;
        }
    }
}
