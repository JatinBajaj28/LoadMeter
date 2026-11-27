# Week 11 Project Journal — LoadMeter

## Week 11 — End-to-End Integration and Release Preparation

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Krish Sahni (1024170390) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 11 |

### Objective for the Week

Prepare a repeatable demonstration and document the expected behavior of the deployment.

Release preparation brought component work back into a single operating sequence. The sample application provides a reproducible target. Its database needs to be available when the live API uses it. The gateway needs its persistence dependency and separate listeners for the REST API, agent tunnel, and public traffic. The desktop agent must know its local destination and be able to register with the gateway before the planned load test can use that route.

Good preparation also requires an order for verifying the system. A direct target check establishes baseline behavior. A short forwarded test checks the joined path. A higher-volume run adds performance evidence after the environment has passed those simpler checks. The team can then use screenshots and recorded test settings to explain the outcome, including cases with failed attempts. This separates demonstration preparation from stress-test interpretation and gives another team member a practical sequence to repeat. The same sequence can be used after setup changes to check that the components still work together.

### Individual Contribution

**Regression preparation and evidence.** I organized regression coverage for target CRUD, tunnel registration, request forwarding, validation, and results display. A small smoke run should come first so a startup fault does not get buried in high-volume results.

I arranged the final regression plan so the fastest checks come first. The target CRUD cases establish basic application behavior. Registration and a forwarded smoke request establish that traffic reaches the target through the gateway. Input validation and assertion checks come next. Larger benchmark runs belong after those checks because they are harder to diagnose when several dependencies are uncertain.

I organized the release verification around the things that had already been defined as acceptance checks. Predictable target responses verify local API behavior. Registration and a forwarded request verify the routing setup. Invalid inputs and unexpected statuses exercise the validation and assertion paths. Dashboard checks verify that the run can be interpreted after it finishes.

I also wrote down a practical way to record the outcomes. Capture the environment and workload settings before the run, then record what the service or interface actually shows. Attach a screenshot where it adds information, such as a chart or a status that is difficult to describe briefly. For the larger run, keep both attempt throughput and failures so the evidence is balanced. I treated this preparation as a way to reduce uncertainty on demonstration day. If a result differs from the expected one, the log should identify the step and show what can be repeated independently.

### Review or Verification

**Verification focus.** Define which screenshots, metrics, and configuration values to capture during the final run.

### Week 11 Deliverable

A regression matrix and an evidence checklist for the demo.

### What I Took Away

For each result I planned to capture the configured workload, the observed status, and enough detail for someone else to interpret it later. Screenshots can show what the desktop displayed, but the configuration values explain the conditions that produced those results. I also kept expected and actual outcomes separate so the final report could describe limitations and failed attempts accurately.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
