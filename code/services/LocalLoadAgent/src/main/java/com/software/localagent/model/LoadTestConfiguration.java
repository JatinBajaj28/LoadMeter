
package com.software.localagent.model;

import java.util.HashMap;
import java.util.Map;

public class LoadTestConfiguration {

    private String targetUrl;

    private String method = "GET";

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> queryParams = new HashMap<>();

    private String requestBody;

    private String gatewayHost;

    private int gatewayPort = 9100;

    private int totalRequests = 100;

    private int concurrency = 10;

    private int timeoutMillis = 5000;

    private int expectedStatusCode = 200;

    private String expectedBodyContains;

    private Long maxResponseTimeMillis;

    private boolean includeRequestDetails = false;

    public Map<String, Object> toMap() {

        Map<String, Object> request = new HashMap<>();

        request.put("targetUrl", targetUrl);
        request.put("method", method);
        request.put("headers", headers);
        request.put("queryParams", queryParams);
        request.put("gatewayHost", gatewayHost);
        request.put("gatewayPort", gatewayPort);
        request.put("totalRequests", totalRequests);
        request.put("concurrency", concurrency);
        request.put("timeoutMillis", timeoutMillis);
        request.put("expectedStatusCode", expectedStatusCode);
        request.put("includeRequestDetails", includeRequestDetails);

        if (requestBody != null) {
            request.put("requestBody", requestBody);
        }

        if (expectedBodyContains != null) {
            request.put("expectedBodyContains", expectedBodyContains);
        }

        if (maxResponseTimeMillis != null) {
            request.put("maxResponseTimeMillis", maxResponseTimeMillis);
        }

        return request;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getGatewayHost() {
        return gatewayHost;
    }

    public void setGatewayHost(String gatewayHost) {
        this.gatewayHost = gatewayHost;
    }

    public int getGatewayPort() {
        return gatewayPort;
    }

    public void setGatewayPort(int gatewayPort) {
        this.gatewayPort = gatewayPort;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(int expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    public String getExpectedBodyContains() {
        return expectedBodyContains;
    }

    public void setExpectedBodyContains(String expectedBodyContains) {
        this.expectedBodyContains = expectedBodyContains;
    }

    public Long getMaxResponseTimeMillis() {
        return maxResponseTimeMillis;
    }

    public void setMaxResponseTimeMillis(Long maxResponseTimeMillis) {
        this.maxResponseTimeMillis = maxResponseTimeMillis;
    }

    public boolean isIncludeRequestDetails() {
        return includeRequestDetails;
    }

    public void setIncludeRequestDetails(boolean includeRequestDetails) {
        this.includeRequestDetails = includeRequestDetails;
    }
}
