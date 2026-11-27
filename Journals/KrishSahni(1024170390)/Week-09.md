# Week 9 Project Journal — LoadMeter

## Week 9 — Metrics and Execution Dashboard

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Krish Sahni (1024170390) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 9 |

### Objective for the Week

Make the job results useful for diagnosing throughput changes, slow requests, and failures.

By this stage, the project needed to communicate what happened during a workload in a way that a developer could inspect after the run. Attempt totals and elapsed time describe the overall workload. The throughput timeline describes when attempts finished. Latency percentiles and the distribution add information about how much the response durations varied. A result also needs to preserve the count and classification of failures so a fast run with many timeouts can be interpreted accurately.

The desktop monitor brings these measurements together but gives them different jobs. Summary values answer the first questions about the run. Charts help identify changes or unusual groups of results. A scatter plot and paginated request table allow the tester to investigate an example behind those larger patterns. The week therefore involved defining the result data and reviewing how it should appear to the user. The useful outcome was a shared understanding of which measurement each view represents and how to compare it with the underlying results.

### Individual Contribution

**Metrics consistency checks.** I reviewed what a tester can reconcile across the summary, chart, and request results. Passed and failed counts should add up to the reported attempts, and percentile values need to be interpreted alongside the timeout count.

I looked for comparisons that would catch an inconsistent report quickly. Completed attempts should reconcile with the counts shown for successes and failures. The overall rate should make sense in relation to the attempt total and elapsed time. Chart axes and labels should use the same units as the summary so the two views can be compared.

I drafted consistency checks beginning with a known test identifier and an expected number of attempts. The dashboard should display counts that can be reconciled. Durations need to use consistent units, and the throughput values should make sense when read alongside the elapsed time. If a value appears surprising, I want enough result data to investigate an example before drawing conclusions about the target.

I also looked at the limitations of comparing metrics across workloads. A test with many timeouts can have a different latency profile from a run where every request completes normally. A high tail-latency percentile indicates that some measurements are slow, but the result table and failure classifications provide more detail about what happened. I organized the checklist so it asks for the workload configuration and the corresponding summary, chart, and representative rows. That gives the team a useful record if a displayed number changes after an update.

### Review or Verification

**Verification focus.** Define comparisons between summary counts, elapsed time, and the detailed rows.

### Week 9 Deliverable

A dashboard verification checklist for totals, throughput, and percentile interpretation.

### What I Took Away

I also examined what a high P95 or P99 can and cannot tell us. It shows that requests near the slow end of the measured distribution took longer, but it does not identify the exact cause. Timeout counts and individual records provide context for that interpretation. My checklist therefore kept summary arithmetic, chart units, and examples from the request table together.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
