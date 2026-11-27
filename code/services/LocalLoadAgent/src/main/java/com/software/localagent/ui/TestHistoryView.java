
package com.software.localagent.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.software.localagent.api.LoadTestApiClient;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class TestHistoryView extends VBox {

    private final LoadTestApiClient apiClient;
    private final Consumer<String> onTestSelected;

    private final TableView<HistoryItem> historyTable;
    private final ObservableList<HistoryItem> historyItems;

    private final Label statusLabel;
    private final Label pageLabel;

    private final Button previousButton;
    private final Button nextButton;
    private final Button refreshButton;

    private int currentPage = 0;
    private final int pageSize = 10;
    private int totalPages = 0;

    private boolean loading = false;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
                    .withZone(ZoneId.systemDefault());

    public TestHistoryView(
            LoadTestApiClient apiClient,
            Consumer<String> onTestSelected
    ) {
        if (apiClient == null) {
            throw new IllegalArgumentException("API client cannot be null");
        }

        if (onTestSelected == null) {
            throw new IllegalArgumentException(
                    "Test selection callback cannot be null"
            );
        }

        this.apiClient = apiClient;
        this.onTestSelected = onTestSelected;

        setSpacing(15);
        setPadding(new Insets(20));

        Label title = new Label("Test History");
        title.setStyle("""
                -fx-font-size: 24px;
                -fx-font-weight: bold;
                """);

        Label subtitle = new Label(
                "Double-click a test to view its analytics"
        );

        historyTable = new TableView<>();
        historyItems = FXCollections.observableArrayList();
        historyTable.setItems(historyItems);

        TableColumn<HistoryItem, String> idColumn =
                new TableColumn<>("Test ID");

        idColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTestId())
        );
        idColumn.setPrefWidth(260);

        TableColumn<HistoryItem, String> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus())
        );
        statusColumn.setPrefWidth(100);

        TableColumn<HistoryItem, String> createdColumn =
                new TableColumn<>("Created At");

        createdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCreatedAt())
        );
        createdColumn.setPrefWidth(175);

        TableColumn<HistoryItem, String> totalColumn =
                new TableColumn<>("Total");

        totalColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTotalRequests())
        );
        totalColumn.setPrefWidth(80);

        TableColumn<HistoryItem, String> passedColumn =
                new TableColumn<>("Passed");

        passedColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPassedRequests())
        );
        passedColumn.setPrefWidth(80);

        TableColumn<HistoryItem, String> failedColumn =
                new TableColumn<>("Failed");

        failedColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFailedRequests())
        );
        failedColumn.setPrefWidth(80);

        TableColumn<HistoryItem, String> rpsColumn =
                new TableColumn<>("RPS");

        rpsColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRps())
        );
        rpsColumn.setPrefWidth(90);

        historyTable.getColumns().addAll(
                idColumn,
                statusColumn,
                createdColumn,
                totalColumn,
                passedColumn,
                failedColumn,
                rpsColumn
        );

        historyTable.setColumnResizePolicy(
                TableView.UNCONSTRAINED_RESIZE_POLICY
        );

        VBox.setVgrow(historyTable, Priority.ALWAYS);

        // Open a test when its row is double-clicked.
        historyTable.setRowFactory(table -> {
            TableRow<HistoryItem> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    HistoryItem selected = row.getItem();

                    onTestSelected.accept(selected.getTestId());
                }
            });

            return row;
        });

        statusLabel = new Label("Loading test history...");
        pageLabel = new Label("Page 1");

        previousButton = new Button("Previous");
        nextButton = new Button("Next");
        refreshButton = new Button("Refresh");

        previousButton.setOnAction(event -> {
            if (currentPage > 0 && !loading) {
                currentPage--;
                loadHistory();
            }
        });

        nextButton.setOnAction(event -> {
            if (currentPage + 1 < totalPages && !loading) {
                currentPage++;
                loadHistory();
            }
        });

        refreshButton.setOnAction(event -> {
            if (!loading) {
                currentPage = 0;
                loadHistory();
            }
        });

        HBox pagination = new HBox(
                10,
                previousButton,
                pageLabel,
                nextButton,
                refreshButton
        );

        getChildren().addAll(
                title,
                subtitle,
                historyTable,
                statusLabel,
                pagination
        );

        loadHistory();
    }

    private void loadHistory() {
        if (loading) {
            return;
        }

        loading = true;

        refreshButton.setDisable(true);
        previousButton.setDisable(true);
        nextButton.setDisable(true);

        statusLabel.setText("Loading test history...");

        apiClient.getTestHistory(currentPage, pageSize)
                .whenComplete((response, error) ->
                        Platform.runLater(() -> {
                            loading = false;
                            refreshButton.setDisable(false);

                            if (error != null) {
                                statusLabel.setText(
                                        "Unable to load test history: "
                                                + getErrorMessage(error)
                                );

                                updatePagination();
                                return;
                            }

                            updateHistory(response);
                        })
                );
    }

    private void updateHistory(JsonNode response) {
        JsonNode content = response.path("content");

        if (!content.isArray()) {
            statusLabel.setText(
                    "Invalid history response from backend."
            );

            updatePagination();
            return;
        }

        historyItems.clear();

        for (JsonNode item : content) {
            JsonNode result = item.path("result");

            HistoryItem historyItem = new HistoryItem(
                    item.path("testId").asText(""),
                    item.path("status").asText("UNKNOWN"),
                    formatDate(item.path("createdAt")),
                    getMetric(result, "totalRequests"),
                    getMetric(result, "passedRequests"),
                    getMetric(result, "failedRequests"),
                    getRps(result)
            );

            historyItems.add(historyItem);
        }

        totalPages = response.path("totalPages").asInt(0);

        int totalElements = response.path("totalElements").asInt(0);

        if (totalElements == 0) {
            statusLabel.setText("No test history available yet.");
        } else {
            statusLabel.setText("Total tests: " + totalElements);
        }

        updatePagination();
    }

    private String getMetric(JsonNode result, String field) {
        JsonNode value = result.path(field);

        if (value.isMissingNode() || value.isNull()) {
            return "-";
        }

        return value.asText();
    }

    private String getRps(JsonNode result) {
        JsonNode value = result.path("requestsPerSecond");

        if (value.isMissingNode() || value.isNull()) {
            return "-";
        }

        return String.format("%.2f", value.asDouble());
    }

    private void updatePagination() {
        pageLabel.setText(
                "Page " + (currentPage + 1)
                        + " of " + Math.max(totalPages, 1)
        );

        previousButton.setDisable(
                loading || currentPage <= 0
        );

        nextButton.setDisable(
                loading || currentPage + 1 >= totalPages
        );
    }

    private String formatDate(JsonNode value) {
        if (value == null || value.isNull()) {
            return "-";
        }

        try {
            Instant instant = Instant.parse(value.asText());
            return DATE_FORMAT.format(instant);
        } catch (Exception e) {
            return value.asText("-");
        }
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

    private static class HistoryItem {

        private final String testId;
        private final String status;
        private final String createdAt;
        private final String totalRequests;
        private final String passedRequests;
        private final String failedRequests;
        private final String rps;

        public HistoryItem(
                String testId,
                String status,
                String createdAt,
                String totalRequests,
                String passedRequests,
                String failedRequests,
                String rps
        ) {
            this.testId = testId;
            this.status = status;
            this.createdAt = createdAt;
            this.totalRequests = totalRequests;
            this.passedRequests = passedRequests;
            this.failedRequests = failedRequests;
            this.rps = rps;
        }

        public String getTestId() {
            return testId;
        }

        public String getStatus() {
            return status;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getTotalRequests() {
            return totalRequests;
        }

        public String getPassedRequests() {
            return passedRequests;
        }

        public String getFailedRequests() {
            return failedRequests;
        }

        public String getRps() {
            return rps;
        }
    }
}
