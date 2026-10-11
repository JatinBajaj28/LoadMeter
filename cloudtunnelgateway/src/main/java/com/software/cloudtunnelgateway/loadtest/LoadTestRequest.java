package com.software.cloudtunnelgateway.loadtest;

import java.util.Map;

public class LoadTestRequest {

    // Student application endpoint
    private String targetUrl;

    // HTTP request configuration
    private String method = "GET";
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String requestBody;

    // Gateway connection configuration
    private String gatewayHost = "localhost";
    private int gatewayPort = 9100;

    // Load configuration
    private int totalRequests = 100;
    private int concurrency = 10;
    private int timeoutMillis = 5000;

    // Response validation criteria
    private Integer expectedStatusCode = 200;
    private String expectedBodyContains;
    private Integer maxResponseTimeMillis;

    public LoadTestRequest() {
    }

    // Target URL

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    // HTTP method

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    // Headers

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    // Query parameters

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(
            Map<String, String> queryParams
    ) {
        this.queryParams = queryParams;
    }

    // Request body

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    // Gateway host

    public String getGatewayHost() {
        return gatewayHost;
    }

    public void setGatewayHost(String gatewayHost) {
        this.gatewayHost = gatewayHost;
    }

    // Gateway port

    public int getGatewayPort() {
        return gatewayPort;
    }

    public void setGatewayPort(int gatewayPort) {
        this.gatewayPort = gatewayPort;
    }

    // Total requests

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    // Concurrency

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    // Request timeout

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    // Expected HTTP status

    public Integer getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(
            Integer expectedStatusCode
    ) {
        this.expectedStatusCode = expectedStatusCode;
    }

    // Expected response body content

    public String getExpectedBodyContains() {
        return expectedBodyContains;
    }

    public void setExpectedBodyContains(
            String expectedBodyContains
    ) {
        this.expectedBodyContains = expectedBodyContains;
    }

    // Maximum allowed response time

    public Integer getMaxResponseTimeMillis() {
        return maxResponseTimeMillis;
    }

    public void setMaxResponseTimeMillis(
            Integer maxResponseTimeMillis
    ) {
        this.maxResponseTimeMillis = maxResponseTimeMillis;
    }
}