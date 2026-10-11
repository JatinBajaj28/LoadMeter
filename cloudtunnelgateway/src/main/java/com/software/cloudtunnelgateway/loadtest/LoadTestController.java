package com.software.cloudtunnelgateway.loadtest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/load-tests")
public class LoadTestController {

    private final LoadTestService loadTestService;

    public LoadTestController(LoadTestService loadTestService) {
        this.loadTestService = loadTestService;
    }

    // Start a new asynchronous load test
    @PostMapping
    public ResponseEntity<?> startLoadTest(
            @RequestBody LoadTestRequest request) {

        try {
            LoadTestJob job = loadTestService.startTest(request);

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(Map.of(
                            "testId", job.getTestId(),
                            "status", job.getStatus(),
                            "createdAt", job.getCreatedAt()
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Check the current status of a test
    @GetMapping("/{testId}")
    public ResponseEntity<?> getTestStatus(
            @PathVariable String testId) {

        LoadTestJob job = loadTestService.getJob(testId);

        if (job == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Load test not found",
                            "testId", testId
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "testId", job.getTestId(),
                        "status", job.getStatus(),
                        "createdAt", job.getCreatedAt(),
                        "startedAt", job.getStartedAt() == null
                                ? "" : job.getStartedAt(),
                        "completedAt", job.getCompletedAt() == null
                                ? "" : job.getCompletedAt(),
                        "error", job.getError() == null
                                ? "" : job.getError()
                )
        );
    }

    // Retrieve the final results of a test
    @GetMapping("/{testId}/results")
    public ResponseEntity<?> getTestResults(
            @PathVariable String testId) {

        LoadTestJob job = loadTestService.getJob(testId);

        if (job == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Load test not found"));
        }

        if (job.getStatus() == LoadTestJob.Status.FAILED) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "testId", testId,
                            "status", job.getStatus(),
                            "error", job.getError() == null
                                    ? "Load test failed" : job.getError()
                    ));
        }

        if (job.getStatus() != LoadTestJob.Status.COMPLETED) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "testId", testId,
                            "status", job.getStatus(),
                            "message", "Results are not ready yet"
                    ));
        }

        return ResponseEntity.ok(job.getResult());
    }
}