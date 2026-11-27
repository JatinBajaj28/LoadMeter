
package com.software.cloudtunnelgateway.loadtest;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "load_tests")
public class LoadTestDocument {

    @Id
    private String testId;

    private String status;

    private Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;

    private String error;

    private LoadTestResult result;

    // Required by Spring Data MongoDB
    public LoadTestDocument() {
    }

    public LoadTestDocument(LoadTestJob job) {
        this.testId = job.getTestId();
        updateFrom(job);
    }

    // Update document using the latest job state
    public void updateFrom(LoadTestJob job) {

        this.status = job.getStatus().name();

        this.createdAt = job.getCreatedAt();
        this.startedAt = job.getStartedAt();
        this.completedAt = job.getCompletedAt();

        this.error = job.getError();

        this.result = job.getResult();
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LoadTestResult getResult() {
        return result;
    }

    public void setResult(LoadTestResult result) {
        this.result = result;
    }
}
