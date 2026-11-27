
package com.software.localagent.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.software.localagent.model.LoadTestConfiguration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LoadTestApiClient {

    private static final String DEFAULT_BASE_URL =
            "http://localhost:8081";

    private static final String LOAD_TESTS_ENDPOINT =
            "/api/load-tests";

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public LoadTestApiClient() {
        this(DEFAULT_BASE_URL);
    }

    public LoadTestApiClient(String baseUrl) {

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "Backend URL cannot be empty"
            );
        }

        this.baseUrl = baseUrl.replaceAll("/+$", "");

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        this.objectMapper = new ObjectMapper();
    }

    // -----------------------------------------------------
    // START LOAD TEST
    // -----------------------------------------------------

    public CompletableFuture<JsonNode> startLoadTest(
            Map<String, Object> configuration) {

        try {

            String json = objectMapper.writeValueAsString(
                    configuration
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            baseUrl + LOAD_TESTS_ENDPOINT
                    ))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return sendRequest(request);

        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    // -----------------------------------------------------
    // GET TEST STATUS
    // -----------------------------------------------------

    public CompletableFuture<JsonNode> getTestStatus(
            String testId) {

        String endpoint = LOAD_TESTS_ENDPOINT
                + "/"
                + encodePathSegment(testId);

        return sendGet(endpoint);
    }

    // -----------------------------------------------------
    // GET AGGREGATE TEST RESULTS
    // -----------------------------------------------------

    public CompletableFuture<JsonNode> getTestResults(
            String testId) {

        String endpoint = LOAD_TESTS_ENDPOINT
                + "/"
                + encodePathSegment(testId)
                + "/results";

        return sendGet(endpoint);
    }

    // -----------------------------------------------------
    // GET PAGINATED INDIVIDUAL REQUEST RESULTS
    // -----------------------------------------------------

    public CompletableFuture<JsonNode> getRequestResults(
            String testId,
            int page,
            int size) {

        if (page < 0) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException(
                            "Page cannot be negative"
                    )
            );
        }

        if (size < 1 || size > 1000) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException(
                            "Page size must be between 1 and 1000"
                    )
            );
        }

        String endpoint = LOAD_TESTS_ENDPOINT
                + "/"
                + encodePathSegment(testId)
                + "/request-results?page="
                + page
                + "&size="
                + size;

        return sendGet(endpoint);
    }

    // -----------------------------------------------------
    // GET PAGINATED TEST HISTORY
    // -----------------------------------------------------

    public CompletableFuture<JsonNode> getTestHistory(
            int page,
            int size) {

        if (page < 0) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException(
                            "Page cannot be negative"
                    )
            );
        }

        if (size < 1 || size > 100) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException(
                            "Page size must be between 1 and 100"
                    )
            );
        }

        String endpoint = LOAD_TESTS_ENDPOINT
                + "/history?page="
                + page
                + "&size="
                + size;

        return sendGet(endpoint);
    }

    // -----------------------------------------------------
    // SEND GET REQUEST
    // -----------------------------------------------------

    private CompletableFuture<JsonNode> sendGet(
            String endpoint) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        return sendRequest(request);
    }

    // -----------------------------------------------------
    // SEND HTTP REQUEST
    // -----------------------------------------------------

    private CompletableFuture<JsonNode> sendRequest(
            HttpRequest request) {

        return httpClient.sendAsync(
                request,
                HttpResponse.BodyHandlers.ofString()
        ).thenApply(response -> {

            int statusCode = response.statusCode();
            String responseBody = response.body();

            if (statusCode < 200 || statusCode >= 300) {
                throw new ApiException(
                        statusCode,
                        responseBody,
                        readErrorMessage(responseBody)
                );
            }

            try {

                if (responseBody == null
                        || responseBody.isBlank()) {

                    return objectMapper.createObjectNode();
                }

                return objectMapper.readTree(responseBody);

            } catch (IOException e) {

                throw new RuntimeException(
                        "The gateway returned an unreadable response"
                );
            }
        });
    }

    private String readErrorMessage(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }

        try {
            JsonNode error = objectMapper.readTree(body);
            if (error == null || !error.isObject()) {
                return "";
            }
            String message = error.path("message").asText("");
            return message.isBlank() ? error.path("error").asText("") : message;
        } catch (IOException ignored) {
            return "";
        }
    }

    // -----------------------------------------------------
    // ENCODE PATH SEGMENT
    // -----------------------------------------------------

    private String encodePathSegment(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Test ID cannot be empty"
            );
        }

        return java.net.URLEncoder
                .encode(
                        value,
                        java.nio.charset.StandardCharsets.UTF_8
                )
                .replace("+", "%20");
    }

    // -----------------------------------------------------
    // API EXCEPTION
    // -----------------------------------------------------

    public static class ApiException extends RuntimeException {

        private final int statusCode;
        private final String responseBody;

        public ApiException(
                int statusCode,
                String responseBody) {

            this(statusCode, responseBody, "");
        }

        private ApiException(int statusCode, String responseBody, String detail) {
            super("Gateway returned HTTP " + statusCode + (detail.isBlank() ? "" : " - " + detail));

            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getResponseBody() {
            return responseBody;
        }
    }

    // -----------------------------------------------------
    // START LOAD TEST USING CONFIGURATION MODEL
    // -----------------------------------------------------

    public CompletableFuture<JsonNode> startLoadTest(
            LoadTestConfiguration configuration) {

        if (configuration == null) {
            throw new IllegalArgumentException(
                    "Load test configuration cannot be null"
            );
        }

        return startLoadTest(configuration.toMap());
    }
}
