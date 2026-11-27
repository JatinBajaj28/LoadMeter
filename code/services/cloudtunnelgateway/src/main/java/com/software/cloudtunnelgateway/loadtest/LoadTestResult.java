
package com.software.cloudtunnelgateway.loadtest;

import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoadTestResult {

    private int totalRequests;
    private int passedRequests;
    private int failedRequests;
    private int errorRequests;

    private long totalDurationMillis;
    private double requestsPerSecond;

    private double averageResponseTimeMillis;
    private long minResponseTimeMillis;
    private long maxResponseTimeMillis;

    private long p50ResponseTimeMillis;
    private long p95ResponseTimeMillis;
    private long p99ResponseTimeMillis;

    private Map<String, Integer> failureBreakdown;
    private Map<String, Integer> responseTimeDistribution;

    private List<ThroughputPoint> throughputTimeline;

    /*
     * Request results remain available in application memory.
     * Spring Data MongoDB must not embed them in load_tests.
     *
     * They will be persisted separately in request_results.
     */
    @Transient
    private List<RequestResult> requestResults;

    // Required by Spring Data MongoDB.
    public LoadTestResult() {
        this.failureBreakdown = new LinkedHashMap<>();
        this.responseTimeDistribution = new LinkedHashMap<>();
        this.throughputTimeline = new ArrayList<>();
        this.requestResults = new ArrayList<>();
    }

    // Represents throughput during one interval.
    public static class ThroughputPoint {

        private long elapsedSeconds;
        private int completedRequests;
        private double requestsPerSecond;

        public ThroughputPoint() {
        }

        public ThroughputPoint(
                long elapsedSeconds,
                int completedRequests,
                double requestsPerSecond
        ) {
            this.elapsedSeconds = elapsedSeconds;
            this.completedRequests = completedRequests;
            this.requestsPerSecond = requestsPerSecond;
        }

        public long getElapsedSeconds() {
            return elapsedSeconds;
        }

        public int getCompletedRequests() {
            return completedRequests;
        }

        public double getRequestsPerSecond() {
            return requestsPerSecond;
        }
    }

    // Backward-compatible constructor.
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

        this(
                totalRequests,
                passedRequests,
                failedRequests,
                errorRequests,
                totalDurationMillis,
                requestsPerSecond,
                averageResponseTimeMillis,
                minResponseTimeMillis,
                maxResponseTimeMillis,
                p50ResponseTimeMillis,
                p95ResponseTimeMillis,
                p99ResponseTimeMillis,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyList(),
                requestResults
        );
    }

    // Existing constructor retained for compatibility.
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
            Map<String, Integer> failureBreakdown,
            Map<String, Integer> responseTimeDistribution,
            List<RequestResult> requestResults
    ) {

        this(
                totalRequests,
                passedRequests,
                failedRequests,
                errorRequests,
                totalDurationMillis,
                requestsPerSecond,
                averageResponseTimeMillis,
                minResponseTimeMillis,
                maxResponseTimeMillis,
                p50ResponseTimeMillis,
                p95ResponseTimeMillis,
                p99ResponseTimeMillis,
                failureBreakdown,
                responseTimeDistribution,
                Collections.emptyList(),
                requestResults
        );
    }

    // Constructor with throughput timeline.
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
            Map<String, Integer> failureBreakdown,
            Map<String, Integer> responseTimeDistribution,
            List<ThroughputPoint> throughputTimeline,
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

        this.failureBreakdown = immutableMap(failureBreakdown);
        this.responseTimeDistribution =
                immutableMap(responseTimeDistribution);

        this.throughputTimeline = immutableList(throughputTimeline);
        this.requestResults = immutableList(requestResults);
    }

    private static <K, V> Map<K, V> immutableMap(
            Map<K, V> source
    ) {
        return Collections.unmodifiableMap(
                new LinkedHashMap<>(
                        source == null
                                ? Collections.emptyMap()
                                : source
                )
        );
    }

    private static <T> List<T> immutableList(
            List<T> source
    ) {
        return Collections.unmodifiableList(
                new ArrayList<>(
                        source == null
                                ? Collections.emptyList()
                                : source
                )
        );
    }

    public static LoadTestResult from(
            List<RequestResult> results,
            long totalDurationMillis
    ) {
        return from(results, totalDurationMillis, true);
    }

    public static LoadTestResult from(
            List<RequestResult> results,
            long totalDurationMillis,
            boolean includeRequestDetails
    ) {

        int total = results.size();
        int passed = 0;
        int errors = 0;

        long[] responseTimes = new long[total];

        long totalResponseTime = 0;
        long minResponseTime = Long.MAX_VALUE;
        long maxResponseTime = 0;

        Map<String, Integer> failureBreakdown =
                new LinkedHashMap<>();

        Map<String, Integer> responseTimeDistribution =
                createResponseTimeBuckets();

        for (int i = 0; i < total; i++) {

            RequestResult result = results.get(i);

            if (result.isPassed()) {
                passed++;
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

            if (result.getStatusCode() == -1) {
                errors++;
            }

            long time = Math.max(
                    0,
                    result.getResponseTimeMillis()
            );

            responseTimes[i] = time;

            totalResponseTime += time;

            minResponseTime = Math.min(minResponseTime, time);
            maxResponseTime = Math.max(maxResponseTime, time);

            String bucket = getResponseTimeBucket(time);

            responseTimeDistribution.merge(
                    bucket,
                    1,
                    Integer::sum
            );
        }

        int failed = total - passed;

        Arrays.sort(responseTimes);

        double average = total == 0
                ? 0
                : (double) totalResponseTime / total;

        double rps = totalDurationMillis <= 0
                ? 0
                : total * 1000.0 / totalDurationMillis;

        List<RequestResult> details = includeRequestDetails
                ? results
                : Collections.emptyList();

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
                failureBreakdown,
                responseTimeDistribution,
                Collections.emptyList(),
                details
        );
    }

    private static Map<String, Integer> createResponseTimeBuckets() {

        Map<String, Integer> buckets = new LinkedHashMap<>();

        buckets.put("0-50ms", 0);
        buckets.put("50-100ms", 0);
        buckets.put("100-250ms", 0);
        buckets.put("250-500ms", 0);
        buckets.put("500-1000ms", 0);
        buckets.put("1000ms+", 0);

        return buckets;
    }

    private static String getResponseTimeBucket(long time) {

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

    public Map<String, Integer> getFailureBreakdown() {
        return failureBreakdown;
    }

    public Map<String, Integer> getResponseTimeDistribution() {
        return responseTimeDistribution;
    }

    public List<ThroughputPoint> getThroughputTimeline() {
        return throughputTimeline;
    }

    public List<RequestResult> getRequestResults() {
        return requestResults;
    }
}
