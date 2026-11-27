# Week 4 Project Journal — LoadMeter

## Week 4 — Extreme Programming and Release Planning

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Krish Sahni (1024170390) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 4 |

### Objective for the Week

Break the project into short iterations with testable work and frequent integration.

The methodology discussion took place before all the technical details were settled. The team needed a way to integrate early enough to discover interface mismatches while changes were still manageable. Extreme Programming provided a useful structure through small increments, testable stories, and regular review. A thin working path could give the team feedback sooner than waiting for every screen and backend feature to be finished.

The first useful increment starts the target, connects the agent, and relays one request. That sequence establishes a place to investigate failures before adding a large load. A later increment can accept a job, and another can turn its results into a desktop view. Keeping those milestones small also helps when a component changes its interface. The team can re-run a short scenario, compare the observable result, and decide whether downstream work needs an update. The method becomes valuable when each story ends with something the team can inspect or exercise.

For a review, a teammate should be able to reproduce the scenario from the story's setup and expected behavior. Recording a disagreement at that point gives the team a concrete follow-up to carry into the next iteration.

### Individual Contribution

**Test-first planning.** I used the team's XP approach to pair each feature with a concrete check. The sample target could supply predictable responses so that tunnel failures and HTTP assertion failures could be evaluated separately.

For each planned story, I drafted an example that could be checked at the end of the iteration. A connection story needs evidence that the gateway accepts the agent and that the desktop sees the resulting status. A forwarding story needs evidence that a request reaches the target and the caller gets the corresponding response.

I wrote test examples alongside the proposed stories so the implementation could be checked in small pieces. The connection story calls for checking the gateway and desktop views of the same registration. The forwarding story calls for comparing a direct response from the target with the response received through the gateway. These cases establish a known baseline when the next change is introduced.

I also planned how quickly each check could be repeated. A short smoke request provides faster feedback than a full benchmark after an everyday change. Tests for invalid inputs and unexpected responses can then exercise the acceptance rules without waiting for a long run. I kept a place for the observed output in the checklist because a checkbox alone is hard to revisit. When a check fails, a status, response body, or connection state gives the team something specific to investigate during the next review.

### Review or Verification

**Verification focus.** Outline smoke checks for registration and forwarding, followed by validation and response checks.

### Week 4 Deliverable

An iteration test checklist with observable pass conditions.

### What I Took Away

I also looked for inexpensive ways to catch a regression. A simple target request is quick to repeat after a change to routing or validation. Once the smoke case works, we can add rejected inputs and more complicated response checks. This kept the XP plan connected to testing work the team could carry out during regular integration rather than leaving all verification for the end.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
