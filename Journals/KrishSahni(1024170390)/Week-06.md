# Week 6 Project Journal — LoadMeter

## Week 6 — Tunnel Prototype and Module Integration

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Krish Sahni (1024170390) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 6 |

### Objective for the Week

Connect the service modules and establish a baseline request path through the prototype.

The prototype integration brought together three parts that can be checked independently. The sample API should respond directly on its local port. The JavaFX agent should establish its outbound Netty connection to the gateway. Then a request through the gateway's TCP ingress should reach the same local service through that established route. The goal this week was to understand the basic round trip before making it carry a larger workload.

Understanding the purpose of each listening port helped organize setup and diagnosis. The target normally listens on 8080, the tunnel endpoint accepts the agent on 9090, and the gateway ingress accepts proxied traffic on 9100. The REST port is a separate way to manage a load test and becomes more important when the job workflow is introduced. This separation gave the team a straightforward smoke-test sequence to record. It also helped explain why a reachable port or a connected status confirms only one part of the full route.

### Individual Contribution

**Baseline forwarding checks.** I organized checks around the target on port 8080, the agent connection on 9090, and the gateway ingress on 9100. Checking target behavior directly first provides a baseline for interpreting the tunneled result.

I started the test sequence with the local API because it gives us a known baseline. If the direct request fails, there is no value in immediately testing the gateway route. Once the target responds, a connection check on the tunnel side establishes that the agent can register. The last step exercises the public ingress and compares the observed result with the baseline.

In the smoke checklist, I made the first comparison intentionally simple. Send a request directly to a known endpoint and record what comes back. Set up the agent and tunnel, then send the equivalent request through the gateway ingress. Comparing status and response content makes it possible to describe the result more precisely than just recording that both connections opened.

If the target responds directly and the tunneled request fails, the next check depends on where the failure first appears. An agent that never registers points toward startup or connectivity. An active agent with an unavailable target suggests checking the configured host and port. An unexpected response after a completed forward suggests reviewing the target behavior or input. I added those diagnostic branches to the checklist so the team can use it during integration as well as during the demonstration. They also give the later test report clear language for distinguishing setup problems from application responses.

### Review or Verification

**Verification focus.** Record expected behavior for a reachable target, a disconnected agent, and an unavailable local port.

### Week 6 Deliverable

A smoke-test checklist for the prototype's three main connection points.

### What I Took Away

For troubleshooting, I listed which stage each observation belongs to. Registration feedback tells us about the agent channel. A failed forward after successful registration may point to the target port or stream routing. An unexpected HTTP response indicates that the connection path may be working while the application is doing something different from the test's expectation.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
