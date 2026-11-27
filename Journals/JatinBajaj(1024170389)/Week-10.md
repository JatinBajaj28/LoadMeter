# Week 10 Project Journal — LoadMeter

## Week 10 — Persistence, History, and CSV Export

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Jatin Bajaj (1024170389) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 10 |

### Objective for the Week

Preserve enough information after a run for the team to reopen and inspect its results.

Persistence extended the life of a test beyond the session that created it. A recent run can be queried by its job identifier, associated with a summary, and opened later for further analysis. The gateway's MongoDB model distinguishes a compact summary from individual request records. That distinction is important for a benchmark with thousands of attempts because browsing the list of jobs does not require retrieving every detailed record.

History and exports also change what the team needs to verify. By reopening a result, a user should be able to recognize the same test and compare the headline values with the original view. Pagination should provide a usable way to inspect large sets of detail. An export should represent the selected run and preserve the important outcome data. The desktop source and the final report provide evidence of the intended workflow. Because the archived gateway and demo materials differ in route coverage, checking the final paired client and gateway is part of the deployment verification.

### Individual Contribution

**Gateway result persistence.** I reviewed the split between the MongoDB documents holding load-test summaries and the records holding request details. Keeping those datasets separate lets a history screen retrieve summaries without loading every request.

I traced what gets saved under a test ID as a job moves from its initial state to a completed result. The summary document is useful for quickly retrieving the status and key metrics. The separate request records carry the volume of detail needed for the table and export. This division allows a history list to display useful information without retrieving thousands of records at once.

I traced the use of the test identifier from the first saved job state to the final summary and detailed results. A history view can retrieve a compact list of runs with identifiers and key status information. A separate query can retrieve the detail needed for the selected run. This keeps the list practical even when a handful of runs each contain thousands of attempts.

The persistence mapping also exposed a verification issue. A job can exist in storage before its result is available, so the response for an incomplete run should be distinguishable from a response for a completed one. Once results are ready, the summary and any available request pages should refer to the same test. I recorded those expectations as part of the gateway review. This helps the team investigate whether an issue originates in write timing, result retrieval, or the association between the test identifier and its stored detail.

### Review or Verification

**Verification focus.** Trace the save path from job state to final summary and batched detail records.

### Week 10 Deliverable

A persistence map showing the relationship between a test ID, its summary, and request results.

### What I Took Away

I focused on the relationship between the stored status and the data available to later consumers. A queued or running entry can exist before the complete result is ready. A completed run should lead to the summary and its corresponding request detail when those routes are available. I recorded those expectations as checks for the deployment build.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
