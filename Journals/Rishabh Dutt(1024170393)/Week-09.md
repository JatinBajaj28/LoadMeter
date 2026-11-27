# Week 9 Project Journal — LoadMeter

## Week 9 — Metrics and Execution Dashboard

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Rishabh Dutt (1024170393) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 9 |

### Objective for the Week

Make the job results useful for diagnosing throughput changes, slow requests, and failures.

By this stage, the project needed to communicate what happened during a workload in a way that a developer could inspect after the run. Attempt totals and elapsed time describe the overall workload. The throughput timeline describes when attempts finished. Latency percentiles and the distribution add information about how much the response durations varied. A result also needs to preserve the count and classification of failures so a fast run with many timeouts can be interpreted accurately.

The desktop monitor brings these measurements together but gives them different jobs. Summary values answer the first questions about the run. Charts help identify changes or unusual groups of results. A scatter plot and paginated request table allow the tester to investigate an example behind those larger patterns. The week therefore involved defining the result data and reviewing how it should appear to the user. The useful outcome was a shared understanding of which measurement each view represents and how to compare it with the underlying results.

### Individual Contribution

**Execution monitor and chart views.** I organized the execution view around summary KPIs, throughput over time, outcomes, latency distribution, and request-level detail. The scatter plot helps a tester locate slow attempts, while the paginated table shows the underlying result records.

I organized the monitor so a developer can start with the overall outcome and then investigate an unusual result. The summary shows counts and key metrics. The throughput timeline shows changes during the run, and the outcome and latency charts summarize the distribution of results. The scatter plot and detail table are useful when the user needs to inspect individual requests.

I reviewed the chart order from the perspective of someone opening an unfamiliar run. The first view should tell them which test they are looking at and offer a quick sense of its outcome. If the failure count or duration is unexpected, the timeline and distribution give them a way to narrow the investigation. The request table then exposes details that a summary cannot show for thousands of attempts.

I also considered how the monitor should behave during an incomplete or very small run. A chart may have few points to show. A result may still be loading. In either case, a clear label or message is more useful than leaving the user to guess whether the display is broken. I noted that the scatter plot and table should be treated as complementary views. The plot helps find a group or a slow attempt, and the rows provide details such as the recorded latency, HTTP status, or failure category.

### Review or Verification

**Verification focus.** Assign each chart a question it answers and pair the dashboard with practical navigation and export checks.

### Week 9 Deliverable

An execution-screen review covering KPI labels, charts, and detail views.

### What I Took Away

I also reviewed how the pieces should behave when there is little or no data. A short run may produce only a few plotted points, while a larger run needs pagination in the table. Clear labels and empty-state messages help in both cases. The CSV workflow gives the user a way to continue that investigation outside the desktop application.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
