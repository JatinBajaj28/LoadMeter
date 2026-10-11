package com.software.cloudtunnelgateway.loadtest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadTestResult {

    private final int totalRequests;
    private final int passedRequests;
    private final int failedRequests;
    private final int errorRequests;

    private final long totalDurationMillis;
    private final double requestsPerSecond;

    private final double averageResponseTimeMillis;
    private final long minResponseTimeMillis;
    private final long maxResponseTimeMillis;

    private final long p50ResponseTimeMillis;
    private final long p95ResponseTimeMillis;
    private final long p99ResponseTimeMillis;

    private final List<RequestResult> requestResults;

    public LoadTestResult(
            int totalRequests,
            int passedRequests,
            int failedRequests,
            int errorRequests,
            long totalDurationMillis,
            double requestsPerSecond,
            double averageResponseTimeMillis,
            long minResponseTimeMillis,
            long maxResponseTimeMillis,
            long p50ResponseTimeMillis,
            long p95ResponseTimeMillis,
            long p99ResponseTimeMillis,
            List<RequestResult> requestResults
    ) {
        this.totalRequests = totalRequests;
        this.passedRequests = passedRequests;
        this.failedRequests = failedRequests;
        this.errorRequests = errorRequests;
        this.totalDurationMillis = totalDurationMillis;
        this.requestsPerSecond = requestsPerSecond;
        this.averageResponseTimeMillis = averageResponseTimeMillis;
        this.minResponseTimeMillis = minResponseTimeMillis;
        this.maxResponseTimeMillis = maxResponseTimeMillis;
        this.p50ResponseTimeMillis = p50ResponseTimeMillis;
        this.p95ResponseTimeMillis = p95ResponseTimeMillis;
        this.p99ResponseTimeMillis = p99ResponseTimeMillis;
        this.requestResults = Collections.unmodifiableList(
                new ArrayList<>(requestResults)
        );
    }

    public static LoadTestResult from(
            List<RequestResult> results,
            long totalDurationMillis
    ) {
        int total = results.size();
        int passed = 0;
        int errors = 0;

        List<Long> responseTimes = new ArrayList<>();

        long totalResponseTime = 0;
        long minResponseTime = Long.MAX_VALUE;
        long maxResponseTime = 0;

        for (RequestResult result : results) {
            if (result.isPassed()) {
                passed++;
            }

            if (result.getStatusCode() == -1) {
                errors++;
            }

            long time = result.getResponseTimeMillis();
            responseTimes.add(time);

            totalResponseTime += time;
            minResponseTime = Math.min(minResponseTime, time);
            maxResponseTime = Math.max(maxResponseTime, time);
        }

        int failed = total - passed;

        Collections.sort(responseTimes);

        double average = total == 0
                ? 0
                : (double) totalResponseTime / total;

        double rps = totalDurationMillis <= 0
                ? 0
                : total * 1000.0 / totalDurationMillis;

        return new LoadTestResult(
                total,
                passed,
                failed,
                errors,
                totalDurationMillis,
                rps,
                average,
                total == 0 ? 0 : minResponseTime,
                maxResponseTime,
                percentile(responseTimes, 50),
                percentile(responseTimes, 95),
                percentile(responseTimes, 99),
                results
        );
    }

    private static long percentile(
            List<Long> sortedValues,
            int percentile
    ) {
        if (sortedValues.isEmpty()) {
            return 0;
        }

        int index = (int) Math.ceil(
                percentile / 100.0 * sortedValues.size()
        ) - 1;

        index = Math.max(0, Math.min(index, sortedValues.size() - 1));

        return sortedValues.get(index);
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getPassedRequests() {
        return passedRequests;
    }

    public int getFailedRequests() {
        return failedRequests;
    }

    public int getErrorRequests() {
        return errorRequests;
    }

    public long getTotalDurationMillis() {
        return totalDurationMillis;
    }

    public double getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public double getAverageResponseTimeMillis() {
        return averageResponseTimeMillis;
    }

    public long getMinResponseTimeMillis() {
        return minResponseTimeMillis;
    }

    public long getMaxResponseTimeMillis() {
        return maxResponseTimeMillis;
    }

    public long getP50ResponseTimeMillis() {
        return p50ResponseTimeMillis;
    }

    public long getP95ResponseTimeMillis() {
        return p95ResponseTimeMillis;
    }

    public long getP99ResponseTimeMillis() {
        return p99ResponseTimeMillis;
    }

    public List<RequestResult> getRequestResults() {
        return requestResults;
    }
}