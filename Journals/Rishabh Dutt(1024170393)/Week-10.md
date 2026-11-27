# Week 10 Project Journal — LoadMeter

## Week 10 — Persistence, History, and CSV Export

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Rishabh Dutt (1024170393) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 10 |

### Objective for the Week

Preserve enough information after a run for the team to reopen and inspect its results.

Persistence extended the life of a test beyond the session that created it. A recent run can be queried by its job identifier, associated with a summary, and opened later for further analysis. The gateway's MongoDB model distinguishes a compact summary from individual request records. That distinction is important for a benchmark with thousands of attempts because browsing the list of jobs does not require retrieving every detailed record.

History and exports also change what the team needs to verify. By reopening a result, a user should be able to recognize the same test and compare the headline values with the original view. Pagination should provide a usable way to inspect large sets of detail. An export should represent the selected run and preserve the important outcome data. The desktop source and the final report provide evidence of the intended workflow. Because the archived gateway and demo materials differ in route coverage, checking the final paired client and gateway is part of the deployment verification.

### Individual Contribution

**Desktop history and result retrieval.** I focused on selecting a previous run and reopening its metrics in the desktop client. The client defines calls for history and paginated request results, so the screen and gateway routes need to be checked together on the build used for the demo.

I reviewed the history workflow as a return visit to a run the developer already understands. The list should allow them to pick a test, then load the right summary and detail for that test ID. Pagination needs to feel consistent when moving through the history list or the separate per-request table.

I reviewed how the history view connects a selected row to the monitor. The row needs to identify a prior run clearly enough that the user can reopen it on purpose. Once opened, the monitor should represent the selected test and retrieve its summary or detail when the backend can provide it. For large result sets, the pagination controls should indicate that the displayed rows are only one part of the result.

I also used the JavaFX API client as a checklist of the calls the desktop expects. The client has history and paginated-detail requests, while the older controller snapshot and the final report differ in the coverage they show. That makes the exact demo build relevant to the check. I documented what to observe in the UI if one of those calls fails, including whether the rest of the result remains usable. The export check follows selection so the user can confirm that the file represents the run they meant to inspect.

### Review or Verification

**Verification focus.** Review history selection, page navigation, and the CSV export workflow.

### Week 10 Deliverable

A history and export walkthrough with a client-to-gateway compatibility check.

### What I Took Away

The desktop client defines calls for both history and request-result pages. The older gateway snapshot and the report do not show identical route coverage, so the practical check is to exercise those screens against the build selected for the demonstration. I included the CSV action in the same walkthrough because it gives the user a second way to confirm they selected the intended run.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
