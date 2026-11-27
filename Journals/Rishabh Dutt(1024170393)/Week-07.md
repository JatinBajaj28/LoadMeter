# Week 7 Project Journal — LoadMeter

## Week 7 — Load Test API and Job Lifecycle

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Rishabh Dutt (1024170393) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 7 |

### Objective for the Week

Define how the desktop client submits a run and tracks it to a terminal state.

The next step was to turn working forwarding into something a developer could use for more than one request at a time. The desktop client needs a way to describe a workload and receive an identifier for it. The gateway needs to acknowledge acceptance, schedule the actual work, and make progress and results available as the job changes state. Separate API calls let the application submit the work and remain responsive while waiting.

The job lifecycle provided a shared vocabulary for the backend, test cases, and desktop monitor. A queued job has been accepted and is waiting for execution. A running job is in progress. Completed and failed are terminal states, but they convey different information. A completed run can contain individual failures because the job still produced a result. Aligning those meanings early reduces the risk that a client error, a job failure, and a request-level assertion failure all get displayed to the user in the same way.

### Individual Contribution

**Load test form and submission flow.** I focused on the move from connection setup to the LoadTestView form. Method, target URL, total requests, concurrency, and timeout drive the submitted workload, while optional fields support headers, query parameters, and a body.

I translated each form field into its role in the submitted request. The method and URL identify what to send. Request count and concurrency control how much work is attempted and how much may overlap. The timeout sets the waiting limit for an attempt. Optional headers, query parameters, and a body let the user express more realistic requests.

I grouped the form inputs by what they affect. The HTTP fields define the request sent to the target. The workload controls define how many attempts to generate and how much overlap is allowed. Assertions define how the returned data is evaluated. That grouping helps a developer prepare a basic test without confusing the settings that affect traffic with the ones that affect the pass or fail summary.

I paid attention to the transition between submitting the form and opening the execution monitor. The desktop needs to wait for an accepted response before it has a job identifier to poll. If the submission is rejected, the user should see a useful message and still be able to correct the existing input. Once a run is accepted, the monitor should clearly represent that run so later status polling does not get mistaken for an update to a previous test. I kept these interactions in the verification outline.

### Review or Verification

**Verification focus.** Map form inputs to the gateway request and plan status polling after a test ID is received.

### Week 7 Deliverable

A form-to-API mapping and a submission flow for the desktop client.

### What I Took Away

The handoff after submission needed its own state. Until the gateway returns a test ID, there is nothing for the execution view to poll. Once the ID is available, the view can associate later updates with that specific run. I also included feedback for a rejected submission so the developer can return to the same configuration and correct the problem.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
