# Week 7 Project Journal — LoadMeter

## Week 7 — Load Test API and Job Lifecycle

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Krish Sahni (1024170390) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 7 |

### Objective for the Week

Define how the desktop client submits a run and tracks it to a terminal state.

The next step was to turn working forwarding into something a developer could use for more than one request at a time. The desktop client needs a way to describe a workload and receive an identifier for it. The gateway needs to acknowledge acceptance, schedule the actual work, and make progress and results available as the job changes state. Separate API calls let the application submit the work and remain responsive while waiting.

The job lifecycle provided a shared vocabulary for the backend, test cases, and desktop monitor. A queued job has been accepted and is waiting for execution. A running job is in progress. Completed and failed are terminal states, but they convey different information. A completed run can contain individual failures because the job still produced a result. Aligning those meanings early reduces the risk that a client error, a job failure, and a request-level assertion failure all get displayed to the user in the same way.

### Individual Contribution

**API and validation test cases.** I reviewed the difference between invalid inputs, unknown test IDs, and requests for results before completion. These cases need separate expected outcomes so the client can give meaningful feedback.

I created examples that target one branch of the API flow at a time. A well-formed submission with a connected agent should return an identifier that can be polled. A lookup with an unknown identifier should provide a clear error. A results request sent too early should indicate that the results are still pending.

I built the API checks around a small valid workload and several changes to the way it is submitted or retrieved. The accepted submission should provide a test identifier. Polling that identifier should retrieve status information. Asking for an unknown identifier should fail in a recognizable way, and requesting results while a job is still active should explain that they are not ready.

I paid particular attention to the timing of the test. An asynchronous job may pass through its intermediate states faster than a person can observe them, so the expected outcome should be defined in terms of allowed behavior. If it is important to see a running state, the test needs a suitable workload and a way to capture it. I also included an example with deliberately mismatched expectations so the completed result can demonstrate request-level failures. That test helps verify that a mixed outcome still produces an inspectable job result.

### Review or Verification

**Verification focus.** Write a test matrix for submission, status retrieval, and results retrieval.

### Week 7 Deliverable

API acceptance cases for both the successful path and common errors.

### What I Took Away

I considered asynchronous ordering while defining the checks. A small job may complete before the tester captures every intermediate state, so the test should verify that the response represents a valid state rather than requiring the observer to see each transition. I also included a completed run with a failed assertion to confirm that request-level failures can appear in an otherwise retrievable result.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
