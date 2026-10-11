package com.software.cloudtunnelgateway.loadtest;

public class RequestResult {

    private final boolean passed;
    private final int statusCode;
    private final long responseTimeMillis;
    private final String error;

    public RequestResult(
            boolean passed,
            int statusCode,
            long responseTimeMillis,
            String error
    ) {
        this.passed = passed;
        this.statusCode = statusCode;
        this.responseTimeMillis = responseTimeMillis;
        this.error = error;
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

    public static RequestResult failure(
            String error,
            long responseTimeMillis
    ) {
        return new RequestResult(
                false,
                -1,
                responseTimeMillis,
                error
        );
    }
}