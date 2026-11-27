# Week 10 Project Journal — LoadMeter

## Week 10 — Persistence, History, and CSV Export

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Krish Sahni (1024170390) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 10 |

### Objective for the Week

Preserve enough information after a run for the team to reopen and inspect its results.

Persistence extended the life of a test beyond the session that created it. A recent run can be queried by its job identifier, associated with a summary, and opened later for further analysis. The gateway's MongoDB model distinguishes a compact summary from individual request records. That distinction is important for a benchmark with thousands of attempts because browsing the list of jobs does not require retrieving every detailed record.

History and exports also change what the team needs to verify. By reopening a result, a user should be able to recognize the same test and compare the headline values with the original view. Pagination should provide a usable way to inspect large sets of detail. An export should represent the selected run and preserve the important outcome data. The desktop source and the final report provide evidence of the intended workflow. Because the archived gateway and demo materials differ in route coverage, checking the final paired client and gateway is part of the deployment verification.

### Individual Contribution

**History and exported data checks.** I defined checks for whether a reopened test agrees with its original summary. For exported results, the useful comparison is between the visible dashboard totals and the corresponding CSV values.

I used the test ID as the starting point for reviewing persistence. A tester should be able to recognize the same run when reopening it, and the reopened summary should agree with the original outcome. Checking separate pages of detail also helps catch problems such as repeating the same records or omitting the last portion of a result set.

I planned a check that starts with a completed run while its results are still visible in the monitor. The tester records the identifier, status, and a few key values. They then open the same run through history and compare the recorded figures. If the detail table uses multiple pages, the tester can inspect records near a page boundary to see whether navigation behaves as expected.

I treated export as a data check as well as a desktop action. Open the exported file and compare its summary with the result on screen. If request-level data is present, pick a few recognizable records and compare their values and classification. I also included a check for an incomplete run because a history entry can appear before all data is available. A clear difference between pending and completed results gives testers a better way to report discrepancies and gives users a better explanation of what they can inspect.

### Review or Verification

**Verification focus.** Plan verification for pagination, incomplete results, and missing detail records.

### Week 10 Deliverable

A persistence and export checklist centered on saved test IDs and consistent counts.

### What I Took Away

For CSV, I focused the comparison on a few values that the user can independently inspect. The exported totals should match the display, and representative request records should keep their latency and outcome. I also planned a check for an incomplete run so it is clear what should be available before completion. These checks make it easier to report exactly where an inconsistency appears.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
