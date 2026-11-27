# Week 5 Project Journal — LoadMeter

## Week 5 — Sprint One Component Design

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Rishabh Dutt (1024170393) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 5 |

### Objective for the Week

Design the gateway, local agent, and integration checks as separate parts with clear interfaces.

Sprint One turned the idea of a reverse tunnel into a design the team could explain at the component level. The gateway needs an open connection to a known agent. A proxied request has its own lifecycle and may end long before the agent disconnects. The local agent needs enough information to select the target and associate a forwarded response with the right request. Those roles have to agree even when the team implements each module separately.

The design also needed a dependable sample target. A simple API response provides a reference point for judging whether the route is working. When the target succeeds directly but the ingress request fails, the difference gives the team a starting point for reviewing the connection or stream logic. That made a well-defined component test just as valuable as a diagram. The sprint could use both to establish the first meaningful milestone, a request that goes through the gateway and reaches its caller with the intended response.

### Individual Contribution

**Local agent and connection screen design.** I outlined how the agent would accept a target host and port and display the result of connecting to the gateway. The connection screen needed enough feedback to tell a configuration error from an unavailable gateway.

For the first agent design, I concentrated on the minimum information the user must enter and the feedback they need in return. The target host and port describe the local application. The connection status describes whether the agent has registered with the gateway. Showing both helps avoid treating every failed request as a problem with the gateway itself.

I clarified which information the developer enters and what the client can determine itself. The developer selects the local endpoint the agent should forward to. The application then initiates the gateway connection. The resulting connection state belongs in a prominent place so the developer knows whether to continue to test configuration or investigate setup.

I described how the connection view could communicate transitions without exposing details of framing or stream IDs. While connecting, it should be clear that an action is in progress. On success, the selected target and the active status should still be visible together. If a connection fails, the feedback should help the user decide whether to check local settings or the gateway address. I also recorded that a connected agent only confirms the outbound tunnel. A forwarded request is the check that confirms the configured target answers through it. That distinction gave the interface design a clearer role during the first team integration.

### Review or Verification

**Verification focus.** Define the screen controls, connection states, and handoff to the local forwarding component.

### Week 5 Deliverable

A first-phase agent design and connection view outline.

### What I Took Away

I also considered what should be visible during a transition. A user who has pressed Connect should be able to tell that registration is in progress and see the final result without repeatedly clicking the button. The design note connects those states to the forwarding component so that the screen communicates what the underlying agent is doing without exposing the details of the protocol.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
