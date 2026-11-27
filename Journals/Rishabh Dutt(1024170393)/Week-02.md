# Week 2 Project Journal — LoadMeter

## Week 2 — Requirements and Acceptance Criteria

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Rishabh Dutt (1024170393) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 2 |

### Objective for the Week

Turn the proposal into requirements for a small, demonstrable end-to-end system.

The requirements work divided the workflow into observable steps. A developer selects a local target, connects the agent, submits a workload, monitors the resulting job, and reads the outcome. Each step depends on a different condition. The target needs to be reachable from the agent. The gateway needs a registered connection to the agent. The workload specification must also be valid before the job can be accepted.

A useful requirement states what should happen when those conditions fail. Bad form values should be explained before a test starts. A missing tunnel should prevent dispatch. An HTTP response that differs from an assertion should still appear in the resulting records. These cases gave the team a way to discuss requirements across the interface, gateway, and testing work without relying on internal implementation details. The acceptance criteria also establish what a reviewer should be able to observe during the demonstration, including a job identifier, meaningful progress, and a summary connected to the run that produced it.

### Individual Contribution

**Desktop workflow requirements.** I described the controls needed to choose a target, submit a test, and watch its progress. Connection state and test state belong in separate parts of the interface because a completed job does not imply the tunnel is still connected.

I sketched the sequence in which the user encounters each control. The host and port come first because they establish what the agent can forward to. The test form then describes the HTTP workload, and the execution view follows the specific job created by the submission. This order also gives us a place to show what went wrong at each step.

For the target settings, I wanted the interface to show the actual host and port the agent will use. A returning user may keep the previous settings, but they need to be able to see when those settings are no longer appropriate. For the test form, I separated values required for every submission from optional values such as headers and a request body. That makes a basic GET workload easier to prepare.

I also thought about how the screen should respond when an action cannot proceed. If the tunnel is disconnected, the user needs a clear indication that they should reconnect before submitting. If input is invalid, they need enough information to correct the value. If a run is already accepted, the monitor should identify that job and show its status independently of later connection changes. These cases turned the proposed screen layouts into behavior the team could review alongside the requirements.

### Review or Verification

**Verification focus.** Define what the desktop client shows for disconnected, connected, running, and completed states.

### Week 2 Deliverable

UI requirements for connection feedback, form inputs, and test status.

### What I Took Away

I considered what should happen if the tunnel drops after the form has already been filled. The configuration is still useful, but the connection status must make the interruption visible before another submission. The UI requirements therefore treat editable inputs and connection availability separately. I also included loading and empty states so the screen has meaningful feedback while waiting for data.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
