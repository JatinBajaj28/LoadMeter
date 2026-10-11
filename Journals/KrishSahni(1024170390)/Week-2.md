# Week 2 Project Journal — Distributed Performance Testing Platform

## Sprint Planning & Metrics Formulation

## Objective for the Week

The objective of Week 2 was to begin metrics planning for Sprint 1, clarify the performance metric definitions outlined in the project proposal, and explore how basic latency percentiles could be calculated.

## Day 1 — Sprint 1 Kickoff & Metrics Scope

- Participated in the Sprint 1 kickoff meeting to identify the metrics required for the initial platform deliverable.
- Clarified the definitions of core performance metrics, including Requests Per Second (RPS), throughput, latency, and error rate.

## Day 2 — Metric Collection Research

- Studied the basic data points a worker would need to record for each request, including start time, end time, and HTTP status code.
- Outlined a preliminary structure for storing individual request execution results for future metric aggregation.

## Day 3 — Technical Spike: Latency Percentiles

- Researched how P50, P95, and P99 latency percentiles are derived from observed response times.
- Wrote a small exploratory script to test percentile calculations using a sample list of response latencies.
- Reviewed how percentile measurements can help represent typical response behaviour and slower requests during a performance test.

## Day 4 — Error and Timeout Definitions

- Examined how non-2xx HTTP responses and connection timeouts could be categorised when analysing request outcomes.
- Outlined a preliminary formula for calculating the overall error rate during a test run.

## Day 5 — Interface Discussion on Metrics

- Met with Jatin and Rishabh to discuss the metric fields required by the proposed live dashboard.
- Discussed consistent units for reporting measurements, including milliseconds for latency and seconds for test duration.

## Day 6 — Documentation & Weekly Review

- Documented the metric definitions and notes from the latency percentile experiment.
- Participated in the weekly team review to summarise progress, identify unresolved questions, and prepare priorities for the following week.

## Week 2 Deliverables

- Preliminary list of core performance metrics and their calculation formulas.
- Exploratory script and notes for testing latency percentile calculations.
- Draft structure for recording request-level execution data.
- Initial understanding of error and timeout classification for future metrics processing.

## Individual Contribution

My primary contribution was **metrics planning and initial latency research**. I focused on clarifying the core performance metrics for the initial deliverable, exploring methods for calculating latency percentiles, and outlining the request-level data required for subsequent metrics aggregation.

## Planned Next Steps

- Refine the request-level record structure with the team.
- Validate the percentile calculation approach against additional sample datasets.
- Align metric definitions with the planned worker output and dashboard requirements.

> **Note:** Week 2 focused on metrics planning, exploratory research, and a limited percentile-calculation experiment. The complete metrics aggregation pipeline and real-time reporting capabilities remain future development tasks unless implementation has actually begun.
