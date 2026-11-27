# Week 8 Project Journal — LoadMeter

## Week 8 — Assertions and Request Result Classification

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Jatin Bajaj (1024170389) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 8 |

### Objective for the Week

Define how individual HTTP attempts are evaluated and how their outcomes are reported.

Once a workload can run, the team needs a consistent rule for deciding whether each attempt passed. A request may receive an expected status, receive an unexpected status, violate a configured body assertion, or fail before a response becomes available. The outcome affects the aggregate results and needs to be understandable when the tester opens a detail record. A defined classification gives the project a way to explain a mixed run without treating the entire job as failed.

The sample API gives the project a controlled source of known responses. A valid create request and a rejected blank name provide clear examples with different expectations. Direct API testing helps establish the behavior of the target itself, while a tunneled run adds evidence about forwarding and processing the test configuration. The most useful checks identify which behavior they are exercising and record what was actually observed. This work prepared the project for reporting both summary totals and the details that account for them.

### Individual Contribution

**Gateway request evaluation.** I traced how the asynchronous engine builds HTTP requests and evaluates responses against configured status and body expectations. Timeouts and connection failures need distinct categories because a missing response cannot be evaluated as an ordinary status mismatch.

I mapped HTTP outcomes to the information the engine actually has. A completed HTTP response can be compared with the expected status and any configured body rule. A timeout has no usable response for those assertions. Recording both as a generic failure would make troubleshooting harder because the user would lose the difference between unexpected application behavior and a failure to get a response.

I traced how an individual attempt becomes a record that can be counted and inspected later. An HTTP response gives the gateway a status and, when needed, a body to compare with configured expectations. A connection error or a timeout gives a different kind of evidence. Its record still needs to show that the attempt took place and why the expected response could not be evaluated.

I also thought about what a useful summary means when several types of failure happen during the same run. The job can successfully finish and save a result even if some of its attempts fail. At that point the user should be able to see passed and failed counts, then examine representative details. That requirement influenced how I described the fields associated with each outcome. Classifying failures at the point where their causes are known makes it easier to group results later without trying to infer the cause from a summary number alone.

### Review or Verification

**Verification focus.** Outline the data a request result should carry for response checks and aggregate metrics.

### Week 8 Deliverable

A response classification guide covering successes, failed assertions, and transport errors.

### What I Took Away

The result model also needs to support aggregation later. A request record can contribute a duration, a response status where available, and a classified failure type. Those fields help the dashboard explain why a run with thousands of attempts may complete normally at the job level while still containing examples that failed the user's expectations.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
