# Week 5 Project Journal — LoadMeter

## Week 5 — Sprint One Component Design

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Jatin Bajaj (1024170389) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 5 |

### Objective for the Week

Design the gateway, local agent, and integration checks as separate parts with clear interfaces.

Sprint One turned the idea of a reverse tunnel into a design the team could explain at the component level. The gateway needs an open connection to a known agent. A proxied request has its own lifecycle and may end long before the agent disconnects. The local agent needs enough information to select the target and associate a forwarded response with the right request. Those roles have to agree even when the team implements each module separately.

The design also needed a dependable sample target. A simple API response provides a reference point for judging whether the route is working. When the target succeeds directly but the ingress request fails, the difference gives the team a starting point for reviewing the connection or stream logic. That made a well-defined component test just as valuable as a diagram. The sprint could use both to establish the first meaningful milestone, a request that goes through the gateway and reaches its caller with the intended response.

### Individual Contribution

**Cloud tunnel gateway design.** I focused on how the gateway would keep track of a registered agent and associate a response with the right incoming connection. The design separated the persistent agent channel from the shorter-lived connections carrying proxied requests.

I separated the persistent agent channel from the temporary connections opened for individual requests. The agent channel is a reusable route to the developer's machine. A stream ID tells the gateway and agent which temporary connection a data frame belongs to when several requests share that channel.

I drew a distinction between registration and stream handling in the gateway design. Registration associates the agent with an open Netty channel. Stream handling associates a temporary public connection with a stream identifier that can be carried over that channel. A request and its response must retain the relationship between those two pieces of state until the conversation has ended.

I walked through the closing cases alongside the successful path. When an ingress connection closes, the relevant stream should stop holding resources. When the agent channel drops, the gateway needs to recognize that the route is unavailable and avoid directing later traffic to stale state. The local agent needs enough information to open the target connection and return bytes under the same identifier. I described those handoffs as part of the design because correct cleanup is easier to review when the ownership of each mapping is clear. The resulting sketch also gives the team a reference for following a single request in the integration test.

### Review or Verification

**Verification focus.** Sketch registration, stream assignment, response forwarding, and connection cleanup.

### Week 5 Deliverable

A gateway design for managing the agent connection and the stream lifecycle.

### What I Took Away

I sketched the path from registration through opening a stream, carrying data, and closing the associated connection. The main design concern was keeping replies attached to the correct caller and cleaning up the mapping after a close or disconnect. That gives the local agent a clear contract: create or find the local connection for the stream, pass bytes to the target, and send the response data back under the same stream ID.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
