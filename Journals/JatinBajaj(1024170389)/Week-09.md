# Week 9 Project Journal — LoadMeter

## Week 9 — Metrics and Execution Dashboard

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Jatin Bajaj (1024170389) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 9 |

### Objective for the Week

Make the job results useful for diagnosing throughput changes, slow requests, and failures.

By this stage, the project needed to communicate what happened during a workload in a way that a developer could inspect after the run. Attempt totals and elapsed time describe the overall workload. The throughput timeline describes when attempts finished. Latency percentiles and the distribution add information about how much the response durations varied. A result also needs to preserve the count and classification of failures so a fast run with many timeouts can be interpreted accurately.

The desktop monitor brings these measurements together but gives them different jobs. Summary values answer the first questions about the run. Charts help identify changes or unusual groups of results. A scatter plot and paginated request table allow the tester to investigate an example behind those larger patterns. The week therefore involved defining the result data and reviewing how it should appear to the user. The useful outcome was a shared understanding of which measurement each view represents and how to compare it with the underlying results.

### Individual Contribution

**Aggregation and latency metrics.** I focused on converting finished request records into totals, throughput buckets, and percentile latencies. The throughput timeline groups completions by elapsed time, while the latency distribution groups requests by duration.

I worked through the difference between benchmark-wide totals and time-based chart points. The overall throughput rate summarizes how fast attempts finished across the whole run. The timeline groups those completions into elapsed-time buckets to show when throughput rose or fell. Each is derived from the run data, but they answer different questions.

I began the aggregation work by considering a result record one request at a time. Each record contributes to the attempt count and has an outcome. Its completion time can contribute to a throughput bucket, and its measured duration can contribute to latency summaries where appropriate. The result model needs to preserve enough information to compute those views consistently.

I also considered the difference between a summary value and a chart point. The overall throughput tells the developer how quickly attempts completed across a run. Throughput over time shows how that rate changed and can draw attention to periods worth investigating. The latency percentiles summarize positions in the distribution, while a histogram can show where values cluster. I kept the failure totals beside those metrics because the rate by itself does not describe how much work succeeded. This mapping gave the desktop work a clearer contract and gave the verification plan a concrete set of calculations and labels to review.

### Review or Verification

**Verification focus.** Check that the gateway result model separates attempt totals and efficiency from latency percentiles.

### Week 9 Deliverable

A metrics mapping from request-level records to summary and chart data.

### What I Took Away

Latency adds another way to read the same workload. A percentile describes a position in the distribution, while a histogram shows how durations are spread across ranges. I kept the success and failure totals alongside both views because a busy run can still have many timeouts. The resulting mapping helped clarify which fields the desktop needed from the gateway to build each chart.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
