# Week 2 Project Journal — LoadMeter

## Week 2 — Requirements and Acceptance Criteria

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Krish Sahni (1024170390) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 2 |

### Objective for the Week

Turn the proposal into requirements for a small, demonstrable end-to-end system.

The requirements work divided the workflow into observable steps. A developer selects a local target, connects the agent, submits a workload, monitors the resulting job, and reads the outcome. Each step depends on a different condition. The target needs to be reachable from the agent. The gateway needs a registered connection to the agent. The workload specification must also be valid before the job can be accepted.

A useful requirement states what should happen when those conditions fail. Bad form values should be explained before a test starts. A missing tunnel should prevent dispatch. An HTTP response that differs from an assertion should still appear in the resulting records. These cases gave the team a way to discuss requirements across the interface, gateway, and testing work without relying on internal implementation details. The acceptance criteria also establish what a reviewer should be able to observe during the demonstration, including a job identifier, meaningful progress, and a summary connected to the run that produced it.

### Individual Contribution

**Validation and acceptance checks.** I worked through valid and invalid inputs for request count, concurrency, timeout, and expected HTTP status. The acceptance criteria had to distinguish a rejected test configuration from a completed test with failed requests.

I tried to state each requirement in a way that gave the tester enough information to reproduce it. For example, a concurrency field needs a valid range and a defined response when the value falls outside it. The HTTP status assertion needs a case where the requested status is valid and the server returns a different one.

I drafted paired examples for the requirements that involve numeric inputs. One example uses a permissible value and one uses a value outside the accepted range. This makes it clear what the tester is changing and avoids confusing a validation result with a problem in the network. I used the same approach for assertions by choosing one target response that satisfies the expected status and one that differs from it.

The acceptance criteria also need to say where the evidence appears. An invalid input should produce an understandable rejection. An assertion mismatch should be visible in the run's details once the job has produced results. A connection check should show the observed tunnel state before submission. I added those expected observations to the checklist so a reviewer can explain why a result passes without needing to look at the gateway code. The resulting requirements can support both an API check and a desktop walkthrough.

### Review or Verification

**Verification focus.** Specify checks for invalid limits, a disconnected tunnel, and a valid run with an assertion failure.

### Week 2 Deliverable

An acceptance checklist linking requirements to observable outcomes.

### What I Took Away

The most useful distinction was between problems with the test setup and problems found during a run. A bad request count should stop the submission. A response that fails an assertion belongs in the completed run's results. I used that distinction to organize the acceptance checklist and to clarify what evidence should appear on the desktop screen or in the API response.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
