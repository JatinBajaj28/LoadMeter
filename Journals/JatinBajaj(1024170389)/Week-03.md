# Week 3 Project Journal — LoadMeter

## Week 3 — Tool Research and Architecture Options

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Jatin Bajaj (1024170389) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 3 |

### Objective for the Week

Use relevant tools as reference points and decide what LoadMeter should build itself.

The tool research served two purposes. It gave the team established examples of HTTP workload configuration and performance reporting, and it helped isolate LoadMeter's particular access problem. Testing tools provide useful ideas for defining requests and interpreting outcomes. Reverse-proxy patterns address a different part of the architecture, namely getting traffic to the correct service and returning the response to the originator. The project needed to bring those concerns together around a local API.

The comparison informed the first release scope. A developer should be able to select a target, supply a request count and concurrency level, and see the result of that workload. The project could demonstrate that flow before adding sophisticated scheduling or long-term comparison across releases. This also clarified how to evaluate architecture choices. The important question was whether a proposed component helped the team complete the full route from the gateway to a locally running service and back, while still producing a useful record of what happened.

### Individual Contribution

**k6 and backend workload design.** I used Grafana k6 as a reference for modeling repeated HTTP traffic and presenting performance thresholds. LoadMeter's first release needed a configurable run against one local target, so a simpler asynchronous gateway engine fit the scope.

The k6 comparison prompted me to distinguish the capabilities of a mature load-testing tool from what LoadMeter needed for its first release. A scriptable workload and sophisticated execution strategies can be useful later. The immediate requirement was to accept a defined HTTP workload, send it through a reachable gateway, and make the outcome available through a job identifier.

A feature I found useful in the k6 comparison was the ability to state what traffic will be attempted and how the results will be evaluated. That idea translates to LoadMeter's request specification even with a smaller set of execution options. The workload can retain a stable identity through its method, target, requested count, concurrency limit, timeout, and assertions. The resulting job then has a known input to compare against.

I used that comparison to think about where more elaborate workload logic would live later on. A future controller could split a workload across workers, but the first implementation could treat the gateway as one execution point. That made the biggest unresolved issue more apparent: the gateway still needed a route to the local service. I kept the initial architecture plan centered on a straightforward asynchronous workflow with one known target and a result that can be retrieved after the run.

### Review or Verification

**Verification focus.** Document how request count, concurrency, and timeouts map to the proposed job model.

### Week 3 Deliverable

A backend comparison and a scoped load-generation design.

### What I Took Away

I used a simple workload model with a total attempt count, a concurrency setting, and a timeout. Those fields made it easier to explain when a job should finish and what its progress means. I also carried the target-access requirement into the architecture decision because generating requests was only half of our problem. The requests still had to reach a service bound to localhost.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
