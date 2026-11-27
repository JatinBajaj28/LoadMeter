
package com.software.cloudtunnelgateway.loadtest;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class LoadTestService {

    // Job execution limits
    private static final int MAX_WORKER_THREADS = 4;
    private static final int MAX_QUEUED_TESTS = 20;

    // Per-test resource limits
    private static final int MAX_TOTAL_REQUESTS = 100_000;
    private static final int MAX_CONCURRENCY = 1_000;
    private static final int MAX_TIMEOUT_MILLIS = 60_000;

    private static final int MAX_REQUEST_BODY_BYTES = 1_048_576;
    private static final int MAX_RESPONSE_BODY_BYTES = 1_048_576;

    // Request results are persisted in batches.
    private static final int REQUEST_RESULT_BATCH_SIZE = 1_000;

    // ---------------------------------------------------------
    // COMPLETED REQUEST
    // ---------------------------------------------------------

    private static class CompletedRequest {

        private final RequestResult result;
        private final long completedAtNanos;

        public CompletedRequest(
                RequestResult result,
                long completedAtNanos
        ) {
            this.result = result;
            this.completedAtNanos = completedAtNanos;
        }
    }

    // ---------------------------------------------------------
    // DEPENDENCIES
    // ---------------------------------------------------------

    private final LoadTestRepository loadTestRepository;
    private final RequestResultRepository requestResultRepository;

    // Contains only queued and running jobs.
    // Completed jobs are removed after MongoDB persistence.
    private final Map<String, LoadTestJob> activeJobs =
            new ConcurrentHashMap<>();

    // Bounded thread pool
    private final ThreadPoolExecutor testExecutor =
            new ThreadPoolExecutor(
                    MAX_WORKER_THREADS,
                    MAX_WORKER_THREADS,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(MAX_QUEUED_TESTS),
                    new ThreadPoolExecutor.AbortPolicy()
            );

    public LoadTestService(
            LoadTestRepository loadTestRepository,
            RequestResultRepository requestResultRepository
    ) {
        this.loadTestRepository = loadTestRepository;
        this.requestResultRepository = requestResultRepository;
    }

    // ---------------------------------------------------------
    // START LOAD TEST
    // ---------------------------------------------------------

    public LoadTestJob startTest(LoadTestRequest request) {

        validateRequest(request);

        String testId = UUID.randomUUID().toString();

        LoadTestJob job = new LoadTestJob(testId);

        // Save QUEUED state before scheduling execution.
        LoadTestDocument document = new LoadTestDocument(job);

        loadTestRepository.save(document);

        activeJobs.put(testId, job);

        try {

            testExecutor.submit(() -> executeJob(job, request));

        } catch (RejectedExecutionException e) {

            activeJobs.remove(testId);

            loadTestRepository.deleteById(testId);

            throw new IllegalStateException(
                    "Load test queue is full. Please try again later."
            );
        }

        return job;
    }

    // ---------------------------------------------------------
    // EXECUTE JOB
    // ---------------------------------------------------------

    private void executeJob(
            LoadTestJob job,
            LoadTestRequest request
    ) {

        try {

            // Mark job as RUNNING.
            job.markRunning();

            loadTestRepository.save(
                    new LoadTestDocument(job)
            );

            // Execute actual load test.
            LoadTestResult result = executeTest(request);

            // Persist request details separately, before marking complete.
            if (request.isIncludeRequestDetails()
                    && result.getRequestResults() != null
                    && !result.getRequestResults().isEmpty()) {

                persistRequestResults(
                        job.getTestId(),
                        result.getRequestResults()
                );
            }

            // Mark job as COMPLETED.
            job.markCompleted(result);

            // Persist aggregate analytics without embedded request details.
            loadTestRepository.save(
                    new LoadTestDocument(job)
            );

        } catch (Exception e) {

            // Remove any partially persisted request details.
            try {
                requestResultRepository.deleteByTestId(job.getTestId());
            } catch (DataAccessException cleanupError) {
                // Preserve the original execution/persistence failure.
            }

            job.markFailed(
                    e.getMessage() == null
                            ? "Unknown load-test error"
                            : e.getMessage()
            );

            // Attempt to persist failure state.
            try {

                loadTestRepository.save(
                        new LoadTestDocument(job)
                );

            } catch (DataAccessException persistenceError) {

                // The database is unavailable.
                // The failed job cannot be persisted.
                return;
            }

        } finally {

            // Release completed job and its in-memory result.
            activeJobs.remove(job.getTestId());
        }
    }

    // ---------------------------------------------------------
    // PERSIST INDIVIDUAL REQUEST RESULTS
    // ---------------------------------------------------------

    private void persistRequestResults(
            String testId,
            List<RequestResult> requestResults
    ) {

        for (int start = 0; start < requestResults.size();
             start += REQUEST_RESULT_BATCH_SIZE) {

            int end = Math.min(
                    start + REQUEST_RESULT_BATCH_SIZE,
                    requestResults.size()
            );

            List<RequestResultDocument> batch =
                    new ArrayList<>(end - start);

            for (int index = start; index < end; index++) {

                batch.add(
                        new RequestResultDocument(
                                testId,
                                index + 1,
                                requestResults.get(index)
                        )
                );
            }

            requestResultRepository.saveAll(batch);
        }
    }

    // ---------------------------------------------------------
    // RETRIEVE INDIVIDUAL REQUEST RESULTS
    // ---------------------------------------------------------

    public List<RequestResultDocument> getRequestResults(
            String testId
    ) {
        return requestResultRepository
                .findByTestIdOrderByRequestNumberAsc(testId);
    }

    public Page<RequestResultDocument> getRequestResults(
            String testId,
            int page,
            int size
    ) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 1000) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 1000"
            );
        }

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.ASC,
                        "requestNumber"
                )
        );

        return requestResultRepository
                .findByTestIdOrderByRequestNumberAsc(
                        testId,
                        pageable
                );
    }

    // ---------------------------------------------------------
    // RETRIEVE ACTIVE JOB
    // ---------------------------------------------------------

    public LoadTestJob getJob(String testId) {
        return activeJobs.get(testId);
    }

    // ---------------------------------------------------------
    // RETRIEVE TEST STATUS FROM MONGODB
    // ---------------------------------------------------------

    public LoadTestStatusResponse getTestStatus(String testId) {

        Optional<LoadTestDocument> optionalDocument =
                loadTestRepository.findById(testId);

        if (optionalDocument.isEmpty()) {
            return null;
        }

        LoadTestDocument document = optionalDocument.get();

        return new LoadTestStatusResponse(
                document.getTestId(),
                document.getStatus(),
                document.getCreatedAt(),
                document.getStartedAt(),
                document.getCompletedAt(),
                document.getError()
        );
    }

    // ---------------------------------------------------------
    // RETRIEVE COMPLETE TEST DOCUMENT
    // ---------------------------------------------------------

    public LoadTestDocument getTestDocument(String testId) {

        return loadTestRepository.findById(testId)
                .orElse(null);
    }

    // ---------------------------------------------------------
    // EXECUTE LOAD TEST
    // ---------------------------------------------------------

    private LoadTestResult executeTest(LoadTestRequest request)
            throws InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(
                        Duration.ofMillis(request.getTimeoutMillis())
                )
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        int totalRequests = request.getTotalRequests();
        int concurrency = request.getConcurrency();

        // Bounded queue for completed requests
        BlockingQueue<CompletedRequest> completedQueue =
                new ArrayBlockingQueue<>(concurrency);

        // Retain primitive latency samples for exact percentiles
        long[] responseTimes = new long[totalRequests];

        // Retain individual results only when requested
        List<RequestResult> requestResults =
                request.isIncludeRequestDetails()
                        ? new ArrayList<>(totalRequests)
                        : Collections.emptyList();

        // Failure analytics
        Map<String, Integer> failureBreakdown =
                new LinkedHashMap<>();

        // Response-time distribution
        Map<String, Integer> responseTimeDistribution =
                createResponseTimeBuckets();

        // Throughput tracking
        Map<Long, Integer> throughputCounts =
                new LinkedHashMap<>();

        // Incremental metrics
        int passedRequests = 0;
        int errorRequests = 0;

        long totalResponseTime = 0;
        long minResponseTime = Long.MAX_VALUE;
        long maxResponseTime = 0;

        long testStart = System.nanoTime();

        int launchedRequests = 0;
        int completedRequests = 0;

        // Initially fill available concurrency slots
        while (launchedRequests < totalRequests
                && launchedRequests < concurrency) {

            submitRequest(client, request, completedQueue);

            launchedRequests++;
        }

        // Process completed requests incrementally
        while (completedRequests < totalRequests) {

            CompletedRequest completedRequest =
                    completedQueue.take();

            RequestResult result = completedRequest.result;

            // Record actual completion interval
            long elapsedInterval = TimeUnit.NANOSECONDS.toSeconds(
                    completedRequest.completedAtNanos - testStart
            );

            throughputCounts.merge(
                    elapsedInterval,
                    1,
                    Integer::sum
            );

            // Update success/failure counters
            if (result.isPassed()) {

                passedRequests++;

            } else {

                RequestResult.FailureType failureType =
                        result.getFailureType() == null
                                ? RequestResult.FailureType.UNKNOWN
                                : result.getFailureType();

                failureBreakdown.merge(
                        failureType.name(),
                        1,
                        Integer::sum
                );
            }

            // Count transport-level errors
            if (result.getStatusCode() == -1) {
                errorRequests++;
            }

            // Update latency metrics
            long responseTime = Math.max(
                    0,
                    result.getResponseTimeMillis()
            );

            responseTimes[completedRequests] = responseTime;

            totalResponseTime += responseTime;

            minResponseTime = Math.min(
                    minResponseTime,
                    responseTime
            );

            maxResponseTime = Math.max(
                    maxResponseTime,
                    responseTime
            );

            // Update response-time distribution
            String bucket = getResponseTimeBucket(responseTime);

            responseTimeDistribution.merge(
                    bucket,
                    1,
                    Integer::sum
            );

            // Retain details only when requested
            if (request.isIncludeRequestDetails()) {
                requestResults.add(result);
            }

            completedRequests++;

            // Immediately replace completed request
            if (launchedRequests < totalRequests) {

                submitRequest(client, request, completedQueue);

                launchedRequests++;
            }
        }

        // Calculate total test duration
        long durationNanos = System.nanoTime() - testStart;

        long durationMillis = TimeUnit.NANOSECONDS.toMillis(
                durationNanos
        );

        // Build throughput timeline
        List<LoadTestResult.ThroughputPoint> throughputTimeline =
                buildThroughputTimeline(
                        throughputCounts,
                        durationNanos
                );

        // Sort latency samples for percentile calculations
        Arrays.sort(responseTimes);

        int failedRequests = totalRequests - passedRequests;

        double averageResponseTime = totalRequests == 0
                ? 0
                : (double) totalResponseTime / totalRequests;

        double requestsPerSecond = durationMillis <= 0
                ? 0
                : totalRequests * 1000.0 / durationMillis;

        // Build final result
        return new LoadTestResult(
                totalRequests,
                passedRequests,
                failedRequests,
                errorRequests,
                durationMillis,
                requestsPerSecond,
                averageResponseTime,
                totalRequests == 0 ? 0 : minResponseTime,
                maxResponseTime,
                percentile(responseTimes, 50),
                percentile(responseTimes, 95),
                percentile(responseTimes, 99),
                failureBreakdown,
                responseTimeDistribution,
                throughputTimeline,
                requestResults
        );
    }

    // ---------------------------------------------------------
    // THROUGHPUT TIMELINE
    // ---------------------------------------------------------

    private List<LoadTestResult.ThroughputPoint> buildThroughputTimeline(
            Map<Long, Integer> throughputCounts,
            long durationNanos
    ) {

        List<LoadTestResult.ThroughputPoint> timeline =
                new ArrayList<>();

        if (durationNanos <= 0) {
            return timeline;
        }

        long intervalCount =
                (durationNanos + 999_999_999L) / 1_000_000_000L;

        for (long interval = 0; interval < intervalCount; interval++) {

            int completed = throughputCounts.getOrDefault(
                    interval,
                    0
            );

            long intervalStartNanos =
                    interval * 1_000_000_000L;

            long remainingNanos =
                    durationNanos - intervalStartNanos;

            long actualIntervalNanos = Math.min(
                    1_000_000_000L,
                    remainingNanos
            );

            double intervalSeconds =
                    actualIntervalNanos / 1_000_000_000.0;

            double intervalRps = intervalSeconds <= 0
                    ? 0
                    : completed / intervalSeconds;

            timeline.add(
                    new LoadTestResult.ThroughputPoint(
                            interval + 1,
                            completed,
                            intervalRps
                    )
            );
        }

        return timeline;
    }

    // ---------------------------------------------------------
    // RESPONSE-TIME DISTRIBUTION
    // ---------------------------------------------------------

    private Map<String, Integer> createResponseTimeBuckets() {

        Map<String, Integer> buckets = new LinkedHashMap<>();

        buckets.put("0-50ms", 0);
        buckets.put("50-100ms", 0);
        buckets.put("100-250ms", 0);
        buckets.put("250-500ms", 0);
        buckets.put("500-1000ms", 0);
        buckets.put("1000ms+", 0);

        return buckets;
    }

    private String getResponseTimeBucket(long time) {

        if (time < 50) {
            return "0-50ms";
        }

        if (time < 100) {
            return "50-100ms";
        }

        if (time < 250) {
            return "100-250ms";
        }

        if (time < 500) {
            return "250-500ms";
        }

        if (time < 1000) {
            return "500-1000ms";
        }

        return "1000ms+";
    }

    // ---------------------------------------------------------
    // FAILURE CLASSIFICATION
    // ---------------------------------------------------------

    private RequestResult.FailureType classifyFailure(
            Throwable error
    ) {

        Throwable current = error;

        while (current != null) {

            if (current instanceof HttpTimeoutException
                    || current instanceof SocketTimeoutException) {

                return RequestResult.FailureType.TIMEOUT;
            }

            current = current.getCause();
        }

        current = error;

        while (current != null) {

            if (current instanceof ConnectException
                    || current instanceof UnknownHostException
                    || current instanceof NoRouteToHostException
                    || current instanceof SocketException
                    || current instanceof IOException) {

                return RequestResult.FailureType.CONNECTION_ERROR;
            }

            current = current.getCause();
        }

        return RequestResult.FailureType.UNKNOWN;
    }

    private Throwable getRootCause(Throwable error) {

        Throwable current = error;

        while (current.getCause() != null
                && current.getCause() != current) {

            current = current.getCause();
        }

        return current;
    }

    // ---------------------------------------------------------
    // SUBMIT ASYNCHRONOUS REQUEST
    // ---------------------------------------------------------

    private void submitRequest(
            HttpClient client,
            LoadTestRequest request,
            BlockingQueue<CompletedRequest> completedQueue
    ) {

        executeRequest(client, request)
                .whenComplete((result, error) -> {

                    RequestResult completedResult;

                    if (error != null) {

                        Throwable rootCause = getRootCause(error);

                        String message = rootCause.getMessage() == null
                                ? rootCause.getClass().getSimpleName()
                                : rootCause.getMessage();

                        completedResult = RequestResult.failure(
                                message,
                                0,
                                classifyFailure(error)
                        );

                    } else {

                        completedResult = result;
                    }

                    completedQueue.add(
                            new CompletedRequest(
                                    completedResult,
                                    System.nanoTime()
                            )
                    );
                });
    }

    // ---------------------------------------------------------
    // PERCENTILE CALCULATION
    // ---------------------------------------------------------

    private static long percentile(
            long[] sortedValues,
            int percentile
    ) {

        if (sortedValues.length == 0) {
            return 0;
        }

        int index = (int) Math.ceil(
                percentile / 100.0 * sortedValues.length
        ) - 1;

        index = Math.max(
                0,
                Math.min(index, sortedValues.length - 1)
        );

        return sortedValues[index];
    }

    // ---------------------------------------------------------
    // EXECUTE ONE HTTP REQUEST THROUGH GATEWAY
    // ---------------------------------------------------------

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
            String method = config.getMethod().toUpperCase(Locale.ROOT);

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
                    HttpResponse.BodyHandlers.ofInputStream()
            ).thenApply(response -> {

                try (InputStream inputStream = response.body()) {

                    byte[] responseBytes = inputStream.readNBytes(
                            MAX_RESPONSE_BODY_BYTES + 1
                    );

                    long elapsed = TimeUnit.NANOSECONDS.toMillis(
                            System.nanoTime() - start
                    );

                    if (responseBytes.length > MAX_RESPONSE_BODY_BYTES) {

                        return RequestResult.failure(
                                "Response body exceeds maximum allowed size of 1 MB",
                                elapsed,
                                RequestResult.FailureType.RESPONSE_VALIDATION
                        );
                    }

                    String responseBody = new String(
                            responseBytes,
                            StandardCharsets.UTF_8
                    );

                    boolean validStatus =
                            config.getExpectedStatusCode() == null
                                    || response.statusCode()
                                    == config.getExpectedStatusCode();

                    boolean validBody =
                            config.getExpectedBodyContains() == null
                                    || responseBody.contains(
                                    config.getExpectedBodyContains()
                            );

                    boolean validTime =
                            config.getMaxResponseTimeMillis() == null
                                    || elapsed <= config.getMaxResponseTimeMillis();

                    boolean passed =
                            validStatus && validBody && validTime;

                    String error = null;

                    RequestResult.FailureType failureType =
                            RequestResult.FailureType.NONE;

                    if (!passed) {

                        if (!validStatus) {

                            failureType =
                                    RequestResult.FailureType.HTTP_STATUS;

                            error = "Expected HTTP status "
                                    + config.getExpectedStatusCode()
                                    + " but received "
                                    + response.statusCode();

                        } else {

                            failureType =
                                    RequestResult.FailureType.RESPONSE_VALIDATION;

                            if (!validBody) {

                                failureType = RequestResult.FailureType.RESPONSE_BODY_MISMATCH;
                                error = "Response body did not contain the expected text";

                            } else {

                                error = "Response time exceeded maximum allowed time";
                            }
                        }
                    }

                    return new RequestResult(
                            passed,
                            response.statusCode(),
                            elapsed,
                            error,
                            failureType
                    );

                } catch (IOException e) {

                    long elapsed = TimeUnit.NANOSECONDS.toMillis(
                            System.nanoTime() - start
                    );

                    return RequestResult.failure(
                            "Failed to read response: " + e.getMessage(),
                            elapsed,
                            classifyFailure(e)
                    );
                }

            }).exceptionally(error -> {

                long elapsed = TimeUnit.NANOSECONDS.toMillis(
                        System.nanoTime() - start
                );

                Throwable rootCause = getRootCause(error);

                String message = rootCause.getMessage() == null
                        ? rootCause.getClass().getSimpleName()
                        : rootCause.getMessage();

                return RequestResult.failure(
                        message,
                        elapsed,
                        classifyFailure(error)
                );
            });

        } catch (Exception e) {

            long elapsed = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - start
            );

            return CompletableFuture.completedFuture(
                    RequestResult.failure(
                            e.getMessage() == null
                                    ? "Unknown request error"
                                    : e.getMessage(),
                            elapsed,
                            classifyFailure(e)
                    )
            );
        }
    }

    // ---------------------------------------------------------
    // BUILD GATEWAY TARGET URI
    // ---------------------------------------------------------

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

                query.append(
                        URLEncoder.encode(
                                entry.getKey(),
                                StandardCharsets.UTF_8
                        )
                );

                query.append("=");

                query.append(
                        URLEncoder.encode(
                                entry.getValue(),
                                StandardCharsets.UTF_8
                        )
                );
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

    // ---------------------------------------------------------
    // VALIDATE LOAD-TEST CONFIGURATION
    // ---------------------------------------------------------

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

        URI target;

        try {

            target = URI.create(request.getTargetUrl());

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Invalid target URL"
            );
        }

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

        if (request.getTotalRequests() < 1) {

            throw new IllegalArgumentException(
                    "Total requests must be at least 1"
            );
        }

        if (request.getTotalRequests() > MAX_TOTAL_REQUESTS) {

            throw new IllegalArgumentException(
                    "Maximum allowed requests per test: "
                            + MAX_TOTAL_REQUESTS
            );
        }

        if (request.getConcurrency() < 1) {

            throw new IllegalArgumentException(
                    "Concurrency must be at least 1"
            );
        }

        if (request.getConcurrency() > MAX_CONCURRENCY) {

            throw new IllegalArgumentException(
                    "Maximum allowed concurrency per test: "
                            + MAX_CONCURRENCY
            );
        }

        if (request.getConcurrency() > request.getTotalRequests()) {

            throw new IllegalArgumentException(
                    "Concurrency cannot exceed total requests"
            );
        }

        if (request.getTimeoutMillis() < 1) {

            throw new IllegalArgumentException(
                    "Timeout must be positive"
            );
        }

        if (request.getTimeoutMillis() > MAX_TIMEOUT_MILLIS) {

            throw new IllegalArgumentException(
                    "Maximum allowed timeout is "
                            + MAX_TIMEOUT_MILLIS + " milliseconds"
            );
        }

        if (request.getMethod() == null
                || !List.of("GET", "POST", "PUT", "DELETE")
                .contains(request.getMethod().toUpperCase(Locale.ROOT))) {

            throw new IllegalArgumentException(
                    "Supported methods: GET, POST, PUT, DELETE"
            );
        }

        if (request.getHeaders() != null) {
            HttpRequest.Builder headerValidator = HttpRequest.newBuilder();
            int position = 0;
            for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
                position++;
                if (header.getKey() == null || header.getValue() == null) {
                    throw new IllegalArgumentException("HTTP header names and values are required");
                }

                try {
                    headerValidator.header(header.getKey(), header.getValue());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Invalid or restricted HTTP header at position " + position
                    );
                }
            }
        }

        if (request.getQueryParams() != null
                && request.getQueryParams().entrySet().stream().anyMatch(
                        param -> param.getKey() == null || param.getKey().isBlank() || param.getValue() == null
                )) {
            throw new IllegalArgumentException("Query parameters require a name and a value");
        }

        String body = request.getRequestBody();

        if (body != null) {

            int bodySize = body.getBytes(StandardCharsets.UTF_8).length;

            if (bodySize > MAX_REQUEST_BODY_BYTES) {

                throw new IllegalArgumentException(
                        "Request body exceeds maximum allowed size of 1 MB"
                );
            }
        }
    }

    // ---------------------------------------------------------
    // RETRIEVE PAGINATED TEST HISTORY
    // ---------------------------------------------------------

    public Page<LoadTestStatusResponse> getTestHistory(
            int page,
            int size
    ) {

        // Validate pagination parameters
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        // Fetch newest tests first
        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Retrieve documents and convert them into lightweight responses
        return loadTestRepository.findAll(pageable)
                .map(document -> new LoadTestStatusResponse(
                        document.getTestId(),
                        document.getStatus(),
                        document.getCreatedAt(),
                        document.getStartedAt(),
                        document.getCompletedAt(),
                        document.getError()
                ));
    }
}

