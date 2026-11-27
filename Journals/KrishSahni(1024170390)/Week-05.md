# Week 5 Project Journal — LoadMeter

## Week 5 — Sprint One Component Design

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Krish Sahni (1024170390) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 5 |

### Objective for the Week

Design the gateway, local agent, and integration checks as separate parts with clear interfaces.

Sprint One turned the idea of a reverse tunnel into a design the team could explain at the component level. The gateway needs an open connection to a known agent. A proxied request has its own lifecycle and may end long before the agent disconnects. The local agent needs enough information to select the target and associate a forwarded response with the right request. Those roles have to agree even when the team implements each module separately.

The design also needed a dependable sample target. A simple API response provides a reference point for judging whether the route is working. When the target succeeds directly but the ingress request fails, the difference gives the team a starting point for reviewing the connection or stream logic. That made a well-defined component test just as valuable as a diagram. The sprint could use both to establish the first meaningful milestone, a request that goes through the gateway and reaches its caller with the intended response.

### Individual Contribution

**Component and integration test design.** I worked through what to check before combining the modules. The target needed a predictable response, the agent needed to report registration, and the gateway needed to relay the same request and response to the caller.

I outlined checks in an order that isolates one dependency at a time. First, the target API should respond directly. Then the agent should establish the gateway connection. Only after those two steps does a request through the ingress tell us something useful about forwarding. If the result differs from the direct response, we can focus on the route and the stream handling.

I arranged the component test plan around checkpoints that reveal different kinds of faults. A direct request to the target confirms that the application is running and can return the expected status. A gateway registration check confirms that the desktop agent can reach the tunnel endpoint. The forwarded case joins those observations by checking what the caller gets back when the ingress sends a request to the same target.

I also outlined the failure checks separately. Disconnecting the agent should make the forwarding route unavailable, while pointing the agent at an unreachable target can produce an error even if registration succeeds. These examples test different layers and should have their own expected evidence. I kept the first integration case deliberately small because a single request is easier to trace from setup to response. Once the route is understood, the team can move to concurrent requests and interpret failures with more context. This sequence was the main practical output of my component design work.

### Review or Verification

**Verification focus.** Define an integration sequence that starts with the target alone and adds one component at a time.

### Week 5 Deliverable

A component test plan and a staged integration checklist.

### What I Took Away

I included failure cases because successful registration does not prove the target is reachable. The plan calls for checking a disconnected agent and an invalid target port separately. I also recorded what result each check should capture, such as the reported connection state, the HTTP status, or the absence of a response. This made the tests useful when preparing the first integration pass.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
