package com.software.cloudtunnelgateway.loadtest;

import java.time.Instant;

public class LoadTestJob {

    public enum Status {
        QUEUED,
        RUNNING,
        COMPLETED,
        FAILED
    }

    private final String testId;
    private volatile Status status;
    private volatile LoadTestResult result;
    private volatile String error;
    private final Instant createdAt;
    private volatile Instant startedAt;
    private volatile Instant completedAt;

    public LoadTestJob(String testId) {
        this.testId = testId;
        this.status = Status.QUEUED;
        this.createdAt = Instant.now();
    }

    public String getTestId() {
        return testId;
    }

    public Status getStatus() {
        return status;
    }

    public LoadTestResult getResult() {
        return result;
    }

    public String getError() {
        return error;
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

    public void markRunning() {
        this.startedAt = Instant.now();
        this.status = Status.RUNNING;
    }

    public void markCompleted(LoadTestResult result) {
        this.result = result;
        this.completedAt = Instant.now();
        this.status = Status.COMPLETED;
    }

    public void markFailed(String error) {
        this.error = error;
        this.completedAt = Instant.now();
        this.status = Status.FAILED;
    }
}