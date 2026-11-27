package com.software.cloudtunnelgateway.loadtest;

import java.time.Instant;

public class LoadTestStatusResponse {

    private final String testId;
    private final String status;
    private final Instant createdAt;
    private final Instant startedAt;
    private final Instant completedAt;
    private final String error;

    public LoadTestStatusResponse(
            String testId,
            String status,
            Instant createdAt,
            Instant startedAt,
            Instant completedAt,
            String error) {

        this.testId = testId;
        this.status = status;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.error = error;
    }

    public String getTestId() {
        return testId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public String getError() {
        return error;
    }

    public static LoadTestStatusResponse from(LoadTestJob job) {
        return new LoadTestStatusResponse(
                job.getTestId(),
                job.getStatus().name(),
                job.getCreatedAt(),
                job.getStartedAt(),
                job.getCompletedAt(),
                job.getError()
        );
    }
}