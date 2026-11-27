# Week 8 Project Journal — LoadMeter

## Week 8 — Assertions and Request Result Classification

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Rishabh Dutt (1024170393) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 8 |

### Objective for the Week

Define how individual HTTP attempts are evaluated and how their outcomes are reported.

Once a workload can run, the team needs a consistent rule for deciding whether each attempt passed. A request may receive an expected status, receive an unexpected status, violate a configured body assertion, or fail before a response becomes available. The outcome affects the aggregate results and needs to be understandable when the tester opens a detail record. A defined classification gives the project a way to explain a mixed run without treating the entire job as failed.

The sample API gives the project a controlled source of known responses. A valid create request and a rejected blank name provide clear examples with different expectations. Direct API testing helps establish the behavior of the target itself, while a tunneled run adds evidence about forwarding and processing the test configuration. The most useful checks identify which behavior they are exercising and record what was actually observed. This work prepared the project for reporting both summary totals and the details that account for them.

### Individual Contribution

**Validation messages and result feedback.** I focused on what the desktop user learns from a rejected configuration or a failed request. Form validation should indicate what to fix, while the execution view should explain what happened during a run.

I considered the timing of each type of message. If an input is invalid or the tunnel is disconnected, the user needs feedback before a run begins. If a request times out or returns an unexpected status after submission, the execution view needs to carry that result alongside successful attempts. Using different places for those messages keeps the workflow understandable.

I looked at the words the desktop could use for a configuration error, a connection problem, and an unsuccessful request. The first two may prevent a run from starting. The last can appear after a run has started and still belong in a completed summary. If the messages all look the same, the developer may try to fix the form when the useful information is actually in the run details.

In the monitor, I focused on giving the user a sensible path from the summary to an example. The summary can show how many attempts passed or failed. A detailed record can show an HTTP status where available, its latency, and a more specific failure type. When the failure is a timeout, the detail view should be understandable even without a normal HTTP status. I carried these cases into the UI test checklist so feedback and result presentation can be reviewed against real outcomes.

### Review or Verification

**Verification focus.** Specify visible feedback for invalid values, a disconnected tunnel, and mixed request outcomes.

### Week 8 Deliverable

A UI test checklist for validation messages and result status display.

### What I Took Away

For the review, I looked at whether a user could tell which value to change and whether an unsuccessful attempt could be inspected after completion. A short failure label in the summary is useful, but it also needs a detailed view where the tester can see the status, latency, or failure type associated with a specific request.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
