# Week 11 Project Journal — LoadMeter

## Week 11 — End-to-End Integration and Release Preparation

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Rishabh Dutt (1024170393) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 11 |

### Objective for the Week

Prepare a repeatable demonstration and document the expected behavior of the deployment.

Release preparation brought component work back into a single operating sequence. The sample application provides a reproducible target. Its database needs to be available when the live API uses it. The gateway needs its persistence dependency and separate listeners for the REST API, agent tunnel, and public traffic. The desktop agent must know its local destination and be able to register with the gateway before the planned load test can use that route.

Good preparation also requires an order for verifying the system. A direct target check establishes baseline behavior. A short forwarded test checks the joined path. A higher-volume run adds performance evidence after the environment has passed those simpler checks. The team can then use screenshots and recorded test settings to explain the outcome, including cases with failed attempts. This separates demonstration preparation from stress-test interpretation and gives another team member a practical sequence to repeat. The same sequence can be used after setup changes to check that the components still work together.

### Individual Contribution

**Desktop demo and report walkthrough.** I prepared the demonstration sequence around connecting, configuring a run, watching its status, and opening its results. Screenshots need to show different parts of that workflow clearly enough to support the report's captions.

I planned the demo around one continuous path. Start from the connection screen, verify the target settings, submit a small workload, then move to the monitor and explain how the result is presented. If the demonstration includes a saved run, the test ID or identifying history row should make it clear which result is being reopened.

I prepared a demonstration that could be followed even by someone seeing the desktop client for the first time. I would begin with the configured local target and connection state, then move to the form and explain which inputs control the workload. After submission, I would show how the monitor gives the user an overview and where to find details for an individual attempt.

I put some thought into what the screenshots should prove. A configuration screenshot helps explain the setup. A summary screenshot captures the result of running a specific workload. A chart or request table shows what the user can inspect afterward. Those images tell a clearer story when each has a distinct purpose and the surrounding explanation identifies the relevant run. I also kept a simple record of the screens that depend on saved results or extra gateway calls, so the demonstration checklist can confirm them against the intended deployment rather than assume every checkout behaves identically.

### Review or Verification

**Verification focus.** Review dashboard screenshots and note which history and export paths should be exercised on the final build.

### Week 11 Deliverable

A concise desktop demo sequence and screenshot plan.

### What I Took Away

I reviewed the purpose of each screenshot before using it as evidence. The configuration screen shows what goes into a run. The monitor shows the outcome. The charts and detailed request view show how to investigate that outcome. Capturing each part once makes the report easier to follow. I left a separate checklist item for comparing the history and export actions against the demo build.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
