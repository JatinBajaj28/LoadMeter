# Week 4 Project Journal — LoadMeter

## Week 4 — Extreme Programming and Release Planning

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Rishabh Dutt (1024170393) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 4 |

### Objective for the Week

Break the project into short iterations with testable work and frequent integration.

The methodology discussion took place before all the technical details were settled. The team needed a way to integrate early enough to discover interface mismatches while changes were still manageable. Extreme Programming provided a useful structure through small increments, testable stories, and regular review. A thin working path could give the team feedback sooner than waiting for every screen and backend feature to be finished.

The first useful increment starts the target, connects the agent, and relays one request. That sequence establishes a place to investigate failures before adding a large load. A later increment can accept a job, and another can turn its results into a desktop view. Keeping those milestones small also helps when a component changes its interface. The team can re-run a short scenario, compare the observable result, and decide whether downstream work needs an update. The method becomes valuable when each story ends with something the team can inspect or exercise.

For a review, a teammate should be able to reproduce the scenario from the story's setup and expected behavior. Recording a disagreement at that point gives the team a concrete follow-up to carry into the next iteration.

### Individual Contribution

**Stories and desktop review points.** I split the desktop work into user stories for connection setup, test configuration, monitoring, and history. Each story gave the team a screen or interaction to review while the gateway interface was still evolving.

I turned the broad idea of a desktop client into smaller stories that could be reviewed individually. The first story was choosing a target and seeing the connection state. Test configuration could follow once the input fields and gateway request contract were clear. Monitoring and history depended on receiving structured result data.

I tried to make each desktop story small enough that a teammate could review it without needing to see the future screens. A connection story can begin with a selected target and end with a reported registration state. A configuration story can begin with a connected agent and end with an accepted or rejected submission. A monitor story can begin with a known job and explain how its result is presented.

Working through that order exposed the information each story needs from the gateway. Configuration depends on a clear request format. Monitoring depends on a job identifier and well-defined states. The charts depend on the result data being available in a useful form. I kept those dependencies in the story notes so the team could revisit the affected screen when the gateway response changed. This makes the review more useful than judging only whether a control looks finished.

### Review or Verification

**Verification focus.** Identify minimum controls and feedback for the first usable connection screen.

### Week 4 Deliverable

A desktop story list ordered by dependencies and user flow.

### What I Took Away

I described each story in terms of what the developer needs to accomplish on screen. For connection setup, the developer should be able to identify which host and port are selected and see useful feedback after pressing Connect. That gives the team something concrete to review early. It also leaves room to revise the later screens when the gateway returns more detail about jobs and results.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
