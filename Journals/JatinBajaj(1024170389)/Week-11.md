# Week 11 Project Journal — LoadMeter

## Week 11 — End-to-End Integration and Release Preparation

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Jatin Bajaj (1024170389) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 11 |

### Objective for the Week

Prepare a repeatable demonstration and document the expected behavior of the deployment.

Release preparation brought component work back into a single operating sequence. The sample application provides a reproducible target. Its database needs to be available when the live API uses it. The gateway needs its persistence dependency and separate listeners for the REST API, agent tunnel, and public traffic. The desktop agent must know its local destination and be able to register with the gateway before the planned load test can use that route.

Good preparation also requires an order for verifying the system. A direct target check establishes baseline behavior. A short forwarded test checks the joined path. A higher-volume run adds performance evidence after the environment has passed those simpler checks. The team can then use screenshots and recorded test settings to explain the outcome, including cases with failed attempts. This separates demonstration preparation from stress-test interpretation and gives another team member a practical sequence to repeat. The same sequence can be used after setup changes to check that the components still work together.

### Individual Contribution

**Service startup and backend readiness.** I connected the deployment view to the actual modules and listening ports. The gateway REST API, Netty listener, and public ingress serve different purposes, and checking them independently helps narrow down startup or routing errors.

I wrote the startup sequence around the dependencies rather than around whichever screen is opened first. The sample application's database should be available before checking its API. The gateway needs its persistence dependency and its REST and TCP listeners available. The agent can then connect to the tunnel endpoint and forward to the configured local target.

I wrote the deployment checklist around what each service provides. MySQL supports the live sample API. MongoDB supports the gateway's result persistence. The target, tunnel listener, ingress, and REST API each have a separate responsibility. Naming those roles in the checklist makes it easier for a teammate to tell whether they are checking the right service at a given step.

I put a direct target check before the tunnel check. A successful response from the sample API establishes that the target and its immediate dependencies are working for the chosen request. After the agent registers, a short request through the forwarding path provides evidence that the gateway and agent can reach the target together. I also described the transition to a load-test submission through the REST API. That handoff helps diagnose cases where the tunnel exists but workload submission or result retrieval is failing. The point of the checklist is to make readiness observable before spending time on a benchmark.

### Review or Verification

**Verification focus.** Document service prerequisites, startup order, and what a connected agent should enable.

### Week 11 Deliverable

A release checklist for the gateway, MongoDB, and the local target dependencies.

### What I Took Away

I treated each boundary as an independent readiness check. A response from the gateway REST API does not confirm that the tunnel listener has accepted an agent. An agent can also show a connected state while the target port is wrong. Including a short forwarded request after startup gives stronger evidence that the service stack is ready for the full demonstration.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
