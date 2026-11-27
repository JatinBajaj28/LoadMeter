# Week 4 Project Journal — LoadMeter

## Week 4 — Extreme Programming and Release Planning

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Jatin Bajaj (1024170389) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 4 |

### Objective for the Week

Break the project into short iterations with testable work and frequent integration.

The methodology discussion took place before all the technical details were settled. The team needed a way to integrate early enough to discover interface mismatches while changes were still manageable. Extreme Programming provided a useful structure through small increments, testable stories, and regular review. A thin working path could give the team feedback sooner than waiting for every screen and backend feature to be finished.

The first useful increment starts the target, connects the agent, and relays one request. That sequence establishes a place to investigate failures before adding a large load. A later increment can accept a job, and another can turn its results into a desktop view. Keeping those milestones small also helps when a component changes its interface. The team can re-run a short scenario, compare the observable result, and decide whether downstream work needs an update. The method becomes valuable when each story ends with something the team can inspect or exercise.

For a review, a teammate should be able to reproduce the scenario from the story's setup and expected behavior. Recording a disagreement at that point gives the team a concrete follow-up to carry into the next iteration.

### Individual Contribution

**Backend iteration planning.** I sequenced the backend work from connection registration to a forwarded request and then to load orchestration. The smallest useful increment was a single round trip, which could isolate forwarding problems before concurrency was introduced.

I used the XP discussion to order the backend changes so each increment had a concrete result. Registration came first because forwarding depends on having an active agent. A single successful request came next because it tests the routing path without introducing concurrent traffic. Multiplexing and asynchronous load execution could then build on that known path.

I drafted a backend progression based on the dependencies between tasks. The gateway first needs to accept agent registration. It then needs to bind an incoming connection to a stream and receive the matching response. Only after those two actions make sense does it help to investigate how several streams share a connection under load.

For the review point, I chose a direct observation: the caller should receive the expected response from the sample application through the gateway. That check can be repeated after an interface change and can be narrowed to one component if it starts failing. I also considered when to bring in asynchronous jobs. Their state transitions are easier to understand when the request path already works. This gave the sprint a concrete backend task that could be demonstrated while the broader job and analytics designs continued to develop.

### Review or Verification

**Verification focus.** Define a narrow gateway milestone and list the conditions for completing it.

### Week 4 Deliverable

An iteration plan with a forwarding checkpoint before the load engine.

### What I Took Away

The practical value of this sequencing was easier diagnosis. If the first proxied request fails, we can trace registration, ingress, and the target without also investigating a busy job queue. I wrote the backend milestone around an observable result: the same caller must receive the target's response. That result gives the next sprint a stable starting point for adding more streams.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
