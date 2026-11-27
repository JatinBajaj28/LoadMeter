# Week 2 Project Journal — LoadMeter

## Week 2 — Requirements and Acceptance Criteria

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Jatin Bajaj (1024170389) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 2 |

### Objective for the Week

Turn the proposal into requirements for a small, demonstrable end-to-end system.

The requirements work divided the workflow into observable steps. A developer selects a local target, connects the agent, submits a workload, monitors the resulting job, and reads the outcome. Each step depends on a different condition. The target needs to be reachable from the agent. The gateway needs a registered connection to the agent. The workload specification must also be valid before the job can be accepted.

A useful requirement states what should happen when those conditions fail. Bad form values should be explained before a test starts. A missing tunnel should prevent dispatch. An HTTP response that differs from an assertion should still appear in the resulting records. These cases gave the team a way to discuss requirements across the interface, gateway, and testing work without relying on internal implementation details. The acceptance criteria also establish what a reviewer should be able to observe during the demonstration, including a job identifier, meaningful progress, and a summary connected to the run that produced it.

### Individual Contribution

**Gateway and tunnel requirements.** I translated the request path into requirements for agent registration, forwarding, and cleanup. A disconnected agent had to be a visible state because the gateway cannot send work through a tunnel that is unavailable.

I followed a request across the gateway, tunnel, and target to identify which component owns each failure. A missing registered agent is a gateway availability problem. A valid agent that cannot reach the configured target is a forwarding problem. If the target returns an unexpected HTTP status, the connection may still be healthy and the assertion should record a failed attempt.

I considered what the gateway can reasonably know at each step. Registration tells it that an agent channel exists. It still needs a forwarded request to establish whether the service selected on that machine answers. A valid HTTP response can then be compared with the expected status or body rule. Those are three different observations, and preserving them makes it easier to diagnose a problem without inspecting every system component at once.

I carried this distinction into the acceptance criteria. A missing channel means the gateway cannot dispatch the workload, so the submission should provide useful feedback. A target that becomes unavailable after dispatch affects the relevant attempts, so their outcomes need to remain visible in the result. A finished request stream also needs to release its mapping so that the next request starts cleanly. I wrote those conditions alongside the successful path because a clean end-to-end demo should also show how the system reacts when a dependency is unavailable.

### Review or Verification

**Verification focus.** Define how a missing connection affects submission and what a successful forward must return.

### Week 2 Deliverable

A backend requirement list and failure scenarios for tunnel availability.

### What I Took Away

Writing those cases helped turn broad requirements into things the team could observe. The gateway should reject a submission when it cannot dispatch it, and it should preserve the outcome of each dispatched request. I also included cleanup in the requirements because leaving a stale stream behind can affect later requests even after the original connection has ended.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
