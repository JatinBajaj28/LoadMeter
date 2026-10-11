# Week 3 Project Journal — Distributed Performance Testing Platform

## Research on Apache JMeter and Distributed Testing

### Objective for the Week

The objective of Week 3 was to explore Apache JMeter as an existing performance-testing solution, understand its test-plan structure and distributed execution capabilities, and identify its advantages and limitations in relation to LoadMeter.

### Day 1 — Introduction to Apache JMeter

- Studied Apache JMeter and its role in load and performance testing.
- Explored its graphical interface and test-plan-based approach.
- Identified the major components involved in defining and executing performance tests.

### Day 2 — Test Plans and Request Configuration

- Explored test plans, thread groups, samplers, timers, assertions, and listeners.
- Studied how HTTP requests can be configured using methods, headers, parameters, and request bodies.
- Examined how concurrent users and execution duration can be configured for a test.

### Day 3 — Distributed Testing Architecture

- Studied JMeter's remote testing architecture and the relationship between a controller and remote test engines.
- Explored how multiple machines can participate in generating application traffic.
- Identified the networking and configuration requirements associated with distributed execution.

### Day 4 — Metrics and Result Collection

- Examined how JMeter records response times, throughput, and request failures.
- Explored the role of listeners and result files in analysing test execution.
- Identified useful metrics for LoadMeter's planned reporting and monitoring functionality.

### Day 5 — Analysis of Advantages and Limitations

- Evaluated JMeter's protocol support, test configurability, and distributed execution capabilities.
- Identified potential operational complexity when configuring remote testing infrastructure.
- Examined the problem of accessing a student's localhost application from cloud-based test engines.

### Day 6 — Documentation and Weekly Review

- Documented the findings from the JMeter exploration.
- Identified reusable concepts, including configurable test plans, concurrency, request execution, and result aggregation.
- Summarised the limitations that LoadMeter should address through its cloud gateway and lightweight local agent.

### Week 3 Deliverables

- Research notes on Apache JMeter and its remote testing architecture.
- Preliminary comparison of JMeter's capabilities with LoadMeter's requirements.
- Identification of useful test-configuration and result-collection concepts.
- Notes on the operational complexity of remote testing and local application connectivity.

### Individual Contribution

My primary contribution was **researching Apache JMeter and its distributed testing architecture**. I focused on understanding test plans, concurrent request generation, remote execution, and result collection, and identifying ways LoadMeter could simplify the setup required to test locally hosted applications.

### Planned Next Steps

- Finalise the initial test-configuration parameters for LoadMeter.
- Define the request-level data required for metrics aggregation.
- Investigate how the cloud gateway and local agent can support concurrent requests while maintaining correct request-response mapping.

**Reference:** https://jmeter.apache.org/usermanual/remote-test.html
