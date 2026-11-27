# Week 6 Project Journal — LoadMeter

## Week 6 — Tunnel Prototype and Module Integration

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Jatin Bajaj (1024170389) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 6 |

### Objective for the Week

Connect the service modules and establish a baseline request path through the prototype.

The prototype integration brought together three parts that can be checked independently. The sample API should respond directly on its local port. The JavaFX agent should establish its outbound Netty connection to the gateway. Then a request through the gateway's TCP ingress should reach the same local service through that established route. The goal this week was to understand the basic round trip before making it carry a larger workload.

Understanding the purpose of each listening port helped organize setup and diagnosis. The target normally listens on 8080, the tunnel endpoint accepts the agent on 9090, and the gateway ingress accepts proxied traffic on 9100. The REST port is a separate way to manage a load test and becomes more important when the job workflow is introduced. This separation gave the team a straightforward smoke-test sequence to record. It also helped explain why a reachable port or a connected status confirms only one part of the full route.

### Individual Contribution

**Registration and stream routing.** I focused on the path from Netty agent registration to public TCP ingress. An agent registration identifies the tunnel, while a stream ID identifies an individual proxied connection carried by that tunnel.

I traced the prototype path in three parts. The agent opens an outbound Netty connection to the tunnel endpoint, a caller or the load engine sends traffic to the public ingress, and the agent forwards the bytes to the chosen local target. Returning data follows the same stream mapping in the opposite direction.

I used the integration checkpoint to follow the same request through both sides of the persistent connection. At the gateway ingress, a new connection needs a stream mapping. At the agent, the stream needs a local connection to the target. Return data needs to keep the stream identifier so the gateway can deliver it to the original caller. This made the registries easier to understand than treating the tunnel as one undifferentiated socket.

I also wrote down which observations would establish progress. The agent registering successfully is the first observation. A target response received through the ingress is the next. Confirming that the stream ends cleanly is useful before repeating the request. I kept the source and destination addresses visible in the walkthrough so it can be reused when a teammate runs the modules on another machine. The immediate goal was a reliable baseline that can support later work on the asynchronous test engine.

### Review or Verification

**Verification focus.** Trace a request from gateway ingress through the agent to the local target and back.

### Week 6 Deliverable

A request trace that shows where registration and per-stream routing take place.

### What I Took Away

The trace made it easier to reason about what each registry is for. The agent registry relates a registered agent to its channel, while the stream registry relates an ingress connection to a stream carried on that channel. I used that distinction to organize the smoke check and the cleanup cases. A response delivered to the correct caller is more meaningful evidence than a gateway port merely accepting connections.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
