# Week 6 Project Journal — LoadMeter

## Week 6 — Tunnel Prototype and Module Integration

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Rishabh Dutt (1024170393) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 6 |

### Objective for the Week

Connect the service modules and establish a baseline request path through the prototype.

The prototype integration brought together three parts that can be checked independently. The sample API should respond directly on its local port. The JavaFX agent should establish its outbound Netty connection to the gateway. Then a request through the gateway's TCP ingress should reach the same local service through that established route. The goal this week was to understand the basic round trip before making it carry a larger workload.

Understanding the purpose of each listening port helped organize setup and diagnosis. The target normally listens on 8080, the tunnel endpoint accepts the agent on 9090, and the gateway ingress accepts proxied traffic on 9100. The REST port is a separate way to manage a load test and becomes more important when the job workflow is introduced. This separation gave the team a straightforward smoke-test sequence to record. It also helped explain why a reachable port or a connected status confirms only one part of the full route.

### Individual Contribution

**Agent configuration and connection feedback.** I focused on the desktop flow before the first load run. Target host and port settings belong with the agent, and tunnel registration feedback tells the user when it makes sense to proceed.

I reviewed how saved target settings affect the next session. Remembering the previous host and port reduces setup work, but those saved values should remain visible and editable. A user may have moved the sample API to a different port, and the connection screen is the easiest place to catch that before starting a test.

I looked at target configuration from the perspective of a user who runs more than one service locally. Retaining the previous host and port is convenient, but the user should be able to confirm those values before connecting. An old value can make a healthy tunnel appear broken when the agent is forwarding to an application that is no longer listening there.

For connection feedback, I mapped the observable stages to what the user should see. A connection attempt needs visible acknowledgement. Successful registration should produce a connected indicator. If the connection later ends, the desktop should update the state so the user knows to reconnect before submitting another workload. I also considered what the UI can tell the tester when the gateway is connected but the target is unavailable. Keeping the selected target and the connection status visible together gives the tester useful context when reviewing a forwarded smoke request.

### Review or Verification

**Verification focus.** Map agent connection events to visible status changes and capture which settings should persist.

### Week 6 Deliverable

A connection-screen checklist covering target settings and status feedback.

### What I Took Away

I also separated meaningful connection messages from generic failure text. A connected indicator should reflect the agent's registration state, while a later forwarding failure can still happen if the local service stops. In the desktop checklist, I treated editing settings, reconnecting, and observing a status update as separate actions. This supported a clearer walkthrough of the prototype before the load configuration screen was introduced.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
