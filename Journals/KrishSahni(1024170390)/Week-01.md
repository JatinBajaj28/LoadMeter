# Week 1 Project Journal — LoadMeter

## Week 1 — Project Proposal and Problem Definition

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Krish Sahni (1024170390) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 1 |

### Objective for the Week

Define who needs LoadMeter, what problem it solves, and how the team will evaluate a first release.

The proposal began with a familiar situation in software development. An API may run only on the developer's computer, yet the developer wants to understand what happens when multiple clients use it at the same time. A local script is easy to start, but its work competes with the service for resources. Running the generator elsewhere changes the shape of the problem because the local service still needs a route for receiving and returning traffic.

The first release was framed around three roles. A local agent would connect outward and forward traffic to the selected service. A gateway would accept the tunnel connection and run a defined HTTP workload through it. A sample target would give the team a service with understandable responses for the demonstration. The immediate measure of progress was a complete request and response through that arrangement. More ambitious ideas such as distributing work across multiple workers were useful for the proposal and stayed separate from the first demonstration.

### Individual Contribution

**Research and evaluation criteria.** I focused on what the results must reveal. Throughput counts completed attempts over time, while error counts and percentile latency describe how the service behaves under load.

I broke down a sample result into three questions: how much work was attempted, how much of it succeeded, and how long it took. A high request rate can coexist with a high timeout rate, so the project needed to publish both figures together. I also distinguished concurrency, which describes overlapping requests, from total requests, which describes the size of the run.

I built the evaluation outline around a small scenario the team could explain without specialized performance tooling. Start with a predictable request to the sample service. Record the method, path, attempt count, concurrency, timeout, and expected response. Then check whether the output states what was attempted and provides enough information to interpret an unexpected result. A short run is suitable for that initial check because it is easier to inspect individual outcomes.

I also considered what comparisons would be fair. Two runs with different timeouts or target data could yield different results even if the implementation had stayed the same. Recording the workload settings is therefore part of the evidence. Percentiles help describe how the slower requests differ from typical ones, and error counts show how many attempts did not satisfy expectations. Together those measurements support a more careful explanation than a single headline number. My proposal contribution was to give the team a usable vocabulary and an evaluation outline that could grow with the implementation.

### Review or Verification

**Verification focus.** Define the measurements required for a credible test and record which need per-request data.

### Week 1 Deliverable

A glossary for concurrency, throughput, timeouts, and P50, P95, and P99 latency.

### What I Took Away

To make the proposal measurable, I defined what evidence a small demonstration should capture: the input settings, elapsed time, success and failure counts, and latency measurements. Percentiles were important because the average alone can hide slow requests. This gave the team a concrete way to evaluate the first version without promising a particular throughput target in advance.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
