
package com.software.cloudtunnelgateway.loadtest;

public class RequestResult {

    public enum FailureType {
        NONE,
        HTTP_STATUS,
        TIMEOUT,
        CONNECTION_ERROR,
        RESPONSE_VALIDATION,
        RESPONSE_BODY_MISMATCH,
        UNKNOWN
    }

    private final boolean passed;
    private final int statusCode;
    private final long responseTimeMillis;
    private final String error;
    private final FailureType failureType;

    public RequestResult(
            boolean passed,
            int statusCode,
            long responseTimeMillis,
            String error
    ) {
        this(
                passed,
                statusCode,
                responseTimeMillis,
                error,
                passed ? FailureType.NONE : FailureType.UNKNOWN
        );
    }

    public RequestResult(
            boolean passed,
            int statusCode,
            long responseTimeMillis,
            String error,
            FailureType failureType
    ) {
        this.passed = passed;
        this.statusCode = statusCode;
        this.responseTimeMillis = responseTimeMillis;
        this.error = error;
        this.failureType = failureType != null
                ? failureType
                : FailureType.UNKNOWN;
    }

    public boolean isPassed() {
        return passed;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getResponseTimeMillis() {
        return responseTimeMillis;
    }

    public String getError() {
        return error;
    }

    public FailureType getFailureType() {
        return failureType;
    }

    public static RequestResult failure(
            String error,
            long responseTimeMillis
    ) {
        return failure(
                error,
                responseTimeMillis,
                FailureType.UNKNOWN
        );
    }

    public static RequestResult failure(
            String error,
            long responseTimeMillis,
            FailureType failureType
    ) {
        return new RequestResult(
                false,
                -1,
                responseTimeMillis,
                error,
                failureType
        );
    }
}
