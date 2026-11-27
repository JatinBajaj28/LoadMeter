# Week 7 Project Journal — LoadMeter

## Week 7 — Load Test API and Job Lifecycle

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Jatin Bajaj (1024170389) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 7 |

### Objective for the Week

Define how the desktop client submits a run and tracks it to a terminal state.

The next step was to turn working forwarding into something a developer could use for more than one request at a time. The desktop client needs a way to describe a workload and receive an identifier for it. The gateway needs to acknowledge acceptance, schedule the actual work, and make progress and results available as the job changes state. Separate API calls let the application submit the work and remain responsive while waiting.

The job lifecycle provided a shared vocabulary for the backend, test cases, and desktop monitor. A queued job has been accepted and is waiting for execution. A running job is in progress. Completed and failed are terminal states, but they convey different information. A completed run can contain individual failures because the job still produced a result. Aligning those meanings early reduces the risk that a client error, a job failure, and a request-level assertion failure all get displayed to the user in the same way.

### Individual Contribution

**Asynchronous gateway orchestration.** I focused on the boundary between request submission and execution. The gateway returns a job identifier that the client can use to poll while the engine processes the workload through the tunnel.

I followed the submit, poll, and result flow from the gateway side. Submission accepts a workload specification and creates a job ID. The queued state shows that the work has been accepted and is waiting. The running state shows that the engine is executing it. Completion makes the result available, while a failed job needs an explanation that the client can display.

I looked at the API contract as a handoff between a caller and a worker that may take time to finish. A successful submission creates a job with an identifier so the caller can track exactly the work it requested. The gateway does not need to keep the original submission open until thousands of attempts complete. It can expose progress separately and return the result once the job reaches the appropriate state.

I explored the meaning of failure in that flow. A rejected submission does not start a benchmark. A job failure means the run could not complete normally. A completed job with request errors can still provide the summary and request data needed for diagnosis. I carried these distinctions into the lifecycle outline so the desktop team and the test plan can rely on the same interpretation. Stable state names and an identifier also make it easier to describe what a user should observe when checking the API.

### Review or Verification

**Verification focus.** Trace the transition from QUEUED to RUNNING and then to COMPLETED or FAILED.

### Week 7 Deliverable

A lifecycle outline for job creation, execution, and result availability.

### What I Took Away

Keeping execution separate from the submission call is important for a long benchmark. The client can continue to show status instead of waiting for a single request to return all the results. I also distinguished a failed job from a completed job containing failed individual requests. That distinction affects both the API response and how the dashboard interprets the outcome.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
