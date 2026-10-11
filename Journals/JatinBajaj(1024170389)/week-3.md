# Week 3 Project Journal — Distributed Performance Testing Platform

## Research on Grafana k6 and Load Generation

### Objective for the Week

The objective of Week 3 was to explore Grafana k6 as an existing load-testing solution, understand its load-generation mechanism, examine its performance metrics, and identify concepts that could be adopted in LoadMeter.

### Day 1 — Introduction to Grafana k6

- Studied Grafana k6 and its role in performance testing of APIs and web applications.
- Explored its developer-oriented approach to defining and executing load tests.
- Identified the main components involved in configuring and running a test.

### Day 2 — Virtual Users and Load Configuration

- Explored how virtual users and iterations are used to generate application traffic.
- Studied the configuration of test duration, request rates, and load patterns.
- Examined how different load configurations can simulate varying levels of application demand.

### Day 3 — Performance Metrics Research

- Studied how k6 reports response times, request rates, throughput-related measurements, and failed requests.
- Explored latency percentiles such as P50, P95, and P99.
- Identified useful metrics for evaluating application performance during a test run.

### Day 4 — Cloud Execution and Localhost Connectivity

- Explored the distinction between local and cloud-based load-test execution.
- Examined why a cloud worker cannot directly access an application's localhost on a student's computer.
- Identified the need for a connectivity mechanism between the cloud load generator and the local application.

### Day 5 — Analysis of Advantages and Limitations

- Evaluated k6's scripting capabilities, performance thresholds, and automated testing support.
- Identified that adopting a load generator alone would not solve LoadMeter's local application connectivity requirement.
- Discussed how LoadMeter could simplify the testing workflow by managing load generation in the cloud.

### Day 6 — Documentation and Weekly Review

- Documented the findings from the k6 exploration.
- Identified useful concepts for LoadMeter, including configurable load patterns, latency measurements, and performance thresholds.
- Summarised the findings for comparison with other existing solutions.

### Week 3 Deliverables

- Research notes on Grafana k6 and its load-generation mechanism.
- Preliminary comparison of k6 features with LoadMeter's requirements.
- Identification of useful performance metrics and load-configuration concepts.
- Notes on the limitations of cloud-to-local application connectivity.

### Individual Contribution

My primary contribution was **researching Grafana k6 and its load-generation capabilities**. I focused on understanding virtual users, load configuration, performance metrics, and the networking requirements associated with testing applications hosted on a student's local machine.

### Planned Next Steps

- Refine LoadMeter's initial load-configuration requirements.
- Identify the metrics required from individual requests and aggregated test results.
- Evaluate the performance overhead of forwarding requests through the proposed local agent and tunnel.

**Reference:** https://grafana.com/docs/k6/latest/
