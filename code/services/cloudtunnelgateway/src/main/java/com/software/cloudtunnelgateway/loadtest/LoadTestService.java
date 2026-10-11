package com.software.cloudtunnelgateway.loadtest;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service
public class LoadTestService {

    private final Map<String, LoadTestJob> jobs =
            new ConcurrentHashMap<>();

    private final ExecutorService testExecutor =
            Executors.newFixedThreadPool(4);

    public LoadTestJob startTest(LoadTestRequest request) {
        validateRequest(request);

        String testId = UUID.randomUUID().toString();
        LoadTestJob job = new LoadTestJob(testId);

        jobs.put(testId, job);

        testExecutor.submit(() -> {
            try {
                job.markRunning();

                LoadTestResult result = executeTest(request);

                job.markCompleted(result);
            } catch (Exception e) {
                job.markFailed(
                        e.getMessage() == null
                                ? "Unknown load-test error"
                                : e.getMessage()
                );
            }
        });

        return job;
    }

    public LoadTestJob getJob(String testId) {
        return jobs.get(testId);
    }

    private LoadTestResult executeTest(LoadTestRequest request)
            throws InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(
                        Duration.ofMillis(request.getTimeoutMillis())
                )
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Semaphore limiter =
                new Semaphore(request.getConcurrency());

        List<CompletableFuture<RequestResult>> futures =
                new ArrayList<>();

        long testStart = System.nanoTime();

        for (int i = 0; i < request.getTotalRequests(); i++) {
            limiter.acquire();

            CompletableFuture<RequestResult> future =
                    executeRequest(client, request)
                            .exceptionally(error ->
                                    RequestResult.failure(
                                            error.getMessage(), 0
                                    )
                            )
                            .whenComplete(
                                    (result, error) -> limiter.release()
                            );

            futures.add(future);
        }

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        List<RequestResult> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        long durationMillis = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - testStart
        );

        return LoadTestResult.from(results, durationMillis);
    }

    private CompletableFuture<RequestResult> executeRequest(
            HttpClient client,
            LoadTestRequest config
    ) {
        long start = System.nanoTime();

        try {
            URI uri = buildTargetUri(config);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(
                            Duration.ofMillis(config.getTimeoutMillis())
                    );

            if (config.getHeaders() != null) {
                config.getHeaders().forEach(builder::header);
            }

            String body = config.getRequestBody();
            String method = config.getMethod().toUpperCase();

            switch (method) {
                case "GET" -> builder.GET();

                case "POST" -> builder.POST(
                        HttpRequest.BodyPublishers.ofString(
                                body == null ? "" : body
                        )
                );

                case "PUT" -> builder.PUT(
                        HttpRequest.BodyPublishers.ofString(
                                body == null ? "" : body
                        )
                );

                case "DELETE" -> builder.method(
                        "DELETE",
                        body == null
                                ? HttpRequest.BodyPublishers.noBody()
                                : HttpRequest.BodyPublishers.ofString(body)
                );

                default -> throw new IllegalArgumentException(
                        "Unsupported method: " + method
                );
            }

            return client.sendAsync(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString()
            ).thenApply(response -> {
                long elapsed = TimeUnit.NANOSECONDS.toMillis(
                        System.nanoTime() - start
                );

                boolean validStatus =
                        config.getExpectedStatusCode() == null
                                || response.statusCode()
                                == config.getExpectedStatusCode();

                boolean validBody =
                        config.getExpectedBodyContains() == null
                                || response.body().contains(
                                config.getExpectedBodyContains()
                        );

                boolean validTime =
                        config.getMaxResponseTimeMillis() == null
                                || elapsed <= config.getMaxResponseTimeMillis();

                boolean passed = validStatus && validBody && validTime;

                String error = passed ? null
                        : "Validation failed: status="
                          + response.statusCode()
                          + ", responseTimeMs=" + elapsed;

                return new RequestResult(
                        passed,
                        response.statusCode(),
                        elapsed,
                        error
                );
            }).exceptionally(error -> {
                long elapsed = TimeUnit.NANOSECONDS.toMillis(
                        System.nanoTime() - start
                );

                return RequestResult.failure(
                        error.getMessage(), elapsed
                );
            });

        } catch (Exception e) {
            long elapsed = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - start
            );

            return CompletableFuture.completedFuture(
                    RequestResult.failure(e.getMessage(), elapsed)
            );
        }
    }

    private URI buildTargetUri(LoadTestRequest config) {
        URI target = URI.create(config.getTargetUrl());

        String path = target.getRawPath();

        if (path == null || path.isBlank()) {
            path = "/";
        }

        StringBuilder query = new StringBuilder();

        if (target.getRawQuery() != null) {
            query.append(target.getRawQuery());
        }

        Map<String, String> params = config.getQueryParams();

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (query.length() > 0) {
                    query.append("&");
                }

                query.append(URLEncoder.encode(
                        entry.getKey(), StandardCharsets.UTF_8
                ));

                query.append("=");

                query.append(URLEncoder.encode(
                        entry.getValue(), StandardCharsets.UTF_8
                ));
            }
        }

        String pathAndQuery = path;

        if (query.length() > 0) {
            pathAndQuery += "?" + query;
        }

        return URI.create(
                "http://" + config.getGatewayHost()
                        + ":" + config.getGatewayPort()
                        + pathAndQuery
        );
    }

    private void validateRequest(LoadTestRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Request cannot be null"
            );
        }

        if (request.getTargetUrl() == null
                || request.getTargetUrl().isBlank()) {
            throw new IllegalArgumentException(
                    "Target URL is required"
            );
        }

        URI target = URI.create(request.getTargetUrl());

        if (!"http".equalsIgnoreCase(target.getScheme())
                || target.getHost() == null) {
            throw new IllegalArgumentException(
                    "Target URL must be a valid HTTP URL"
            );
        }

        if (request.getGatewayHost() == null
                || request.getGatewayHost().isBlank()) {
            throw new IllegalArgumentException(
                    "Gateway host is required"
            );
        }

        if (request.getGatewayPort() < 1
                || request.getGatewayPort() > 65535) {
            throw new IllegalArgumentException(
                    "Invalid Gateway port"
            );
        }

        if (request.getTotalRequests() < 1
                || request.getConcurrency() < 1
                || request.getConcurrency() > request.getTotalRequests()) {
            throw new IllegalArgumentException(
                    "Invalid totalRequests or concurrency"
            );
        }

        if (request.getTimeoutMillis() < 1) {
            throw new IllegalArgumentException(
                    "Timeout must be positive"
            );
        }

        if (request.getMethod() == null
                || !List.of("GET", "POST", "PUT", "DELETE")
                .contains(request.getMethod().toUpperCase())) {
            throw new IllegalArgumentException(
                    "Supported methods: GET, POST, PUT, DELETE"
            );
        }
    }
}