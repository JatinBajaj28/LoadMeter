# Week 8 Project Journal — LoadMeter

## Week 8 — Assertions and Request Result Classification

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Krish Sahni (1024170390) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 8 |

### Objective for the Week

Define how individual HTTP attempts are evaluated and how their outcomes are reported.

Once a workload can run, the team needs a consistent rule for deciding whether each attempt passed. A request may receive an expected status, receive an unexpected status, violate a configured body assertion, or fail before a response becomes available. The outcome affects the aggregate results and needs to be understandable when the tester opens a detail record. A defined classification gives the project a way to explain a mixed run without treating the entire job as failed.

The sample API gives the project a controlled source of known responses. A valid create request and a rejected blank name provide clear examples with different expectations. Direct API testing helps establish the behavior of the target itself, while a tunneled run adds evidence about forwarding and processing the test configuration. The most useful checks identify which behavior they are exercising and record what was actually observed. This work prepared the project for reporting both summary totals and the details that account for them.

### Individual Contribution

**Functional cases for the sample API.** I used the student1 API as the test target for a successful create, a rejected blank name, and a normal read. Those predictable cases give the team known results to compare with the load engine's assertions.

I chose functional cases with predictable meanings. A valid create request provides a known success response. A blank name provides a known rejected input. Reading an existing record provides a straightforward way to exercise the target without writing another row. These examples are small enough to repeat when an assertion or forwarding change needs verification.

I designed the functional test examples so the expected target behavior is easy to explain. A valid create request should receive the documented success status. Submitting a blank name should exercise validation. A read request should return a response the tester can use for comparison on the direct route and through the tunnel. Each example gives the resulting assertion a specific reason to pass or fail.

I kept the target's H2-backed tests separate from the manually reviewed end-to-end setup because the two checks cover different parts of the project. The target-level test confirms the API logic with its test database. The tunneled run includes the gateway, agent, forwarding path, and live target setup. If the outcomes disagree, I can use the smaller direct check to narrow the investigation. The functional test matrix records those layers and leaves room for noting the observed status and failure type without claiming that every case ran in the same environment.

### Review or Verification

**Verification focus.** Connect the target's JUnit and H2 checks to the separate end-to-end checks through the tunnel.

### Week 8 Deliverable

A functional test matrix that distinguishes target behavior from forwarding behavior.

### What I Took Away

I separated the target application's own checks from the tunneled integration checks. The JUnit and H2 path can establish how the sample API and its database logic behave under the test setup. The end-to-end path then exercises the agent, gateway, and target together. Comparing those layers helps explain whether a difference belongs to the API behavior, the tunnel, or the load engine's result classification.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
