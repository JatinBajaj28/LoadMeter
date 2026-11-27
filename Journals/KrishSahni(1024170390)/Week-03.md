# Week 3 Project Journal — LoadMeter

## Week 3 — Tool Research and Architecture Options

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Krish Sahni (1024170390) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 3 |

### Objective for the Week

Use relevant tools as reference points and decide what LoadMeter should build itself.

The tool research served two purposes. It gave the team established examples of HTTP workload configuration and performance reporting, and it helped isolate LoadMeter's particular access problem. Testing tools provide useful ideas for defining requests and interpreting outcomes. Reverse-proxy patterns address a different part of the architecture, namely getting traffic to the correct service and returning the response to the originator. The project needed to bring those concerns together around a local API.

The comparison informed the first release scope. A developer should be able to select a target, supply a request count and concurrency level, and see the result of that workload. The project could demonstrate that flow before adding sophisticated scheduling or long-term comparison across releases. This also clarified how to evaluate architecture choices. The important question was whether a proposed component helped the team complete the full route from the gateway to a locally running service and back, while still producing a useful record of what happened.

### Individual Contribution

**JMeter and repeatable testing.** I used Apache JMeter as a reference for grouping requests, adding assertions, and interpreting results. Explicit input settings and expected outcomes make it possible to compare runs under the same conditions.

I looked at how a JMeter-style test setup records request inputs and attaches assertions to the expected response. That approach is useful when a run needs to be reproduced. A tester can describe the method, URL, request count, and expected status before the test, then compare those settings with what the result actually reports.

I took away a practical lesson from the JMeter examples. A test becomes easier to reproduce when the request and its expected behavior are written down together. The test can then specify which part is intended to change, such as the request count, while the other inputs stay comparable. This helps separate a genuine change in behavior from differences introduced by a changed workload.

I applied this to the smoke, functional, and stress examples in our plan. A smoke request uses a known route and gives the team a clear sign that the service path works. A functional case checks how the target and application logic handle expected and unexpected inputs. A larger workload produces enough attempts to look at throughput and latency over time. I also listed the evidence to retain for each case, including selected settings and any errors, so the result can be interpreted later. For example, if a repeat run changes the timeout, I would label it as a separate scenario. That keeps the earlier run useful as a record and shows exactly which setting changed when comparing the two results.

### Review or Verification

**Verification focus.** Record reusable settings for smoke, functional, and higher-load cases.

### Week 3 Deliverable

A test design note based on repeatable inputs and measurable outcomes.

### What I Took Away

For LoadMeter, I organized examples into a quick smoke test, a functional assertion test, and a larger workload. The smoke test checks the route and service availability. The functional cases check that expected and unexpected responses are classified correctly. The larger workload adds pressure after those basics are established. This structure gave the team a simple testing progression to reuse during integration.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
