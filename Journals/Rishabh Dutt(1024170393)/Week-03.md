# Week 3 Project Journal — LoadMeter

## Week 3 — Tool Research and Architecture Options

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Rishabh Dutt (1024170393) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 3 |

### Objective for the Week

Use relevant tools as reference points and decide what LoadMeter should build itself.

The tool research served two purposes. It gave the team established examples of HTTP workload configuration and performance reporting, and it helped isolate LoadMeter's particular access problem. Testing tools provide useful ideas for defining requests and interpreting outcomes. Reverse-proxy patterns address a different part of the architecture, namely getting traffic to the correct service and returning the response to the originator. The project needed to bring those concerns together around a local API.

The comparison informed the first release scope. A developer should be able to select a target, supply a request count and concurrency level, and see the result of that workload. The project could demonstrate that flow before adding sophisticated scheduling or long-term comparison across releases. This also clarified how to evaluate architecture choices. The important question was whether a proposed component helped the team complete the full route from the gateway to a locally running service and back, while still producing a useful record of what happened.

### Individual Contribution

**Nginx and local forwarding research.** I studied reverse-proxy behavior as a starting point for understanding how a local service receives a forwarded request. LoadMeter's desktop agent needed to initiate the connection so a user could test a service without setting up incoming forwarding on their machine.

I used the Nginx research to understand the basic forwarding sequence. A proxy accepts a connection, passes the request to an upstream service, and returns the response. That pattern helped me explain what the local agent needed to do after it established its outbound connection to the gateway.

I used the forwarding research to draw the route from the desktop to the sample application and back. The agent needs the developer's target address so it can open the correct local connection when traffic comes through the tunnel. The gateway needs to know where to send that traffic. From the user's point of view, these details should become a short sequence of settings and a visible connection result.

The important UI decision was to make the local endpoint explicit. A developer might start the service on a different port, and a correctly connected agent would still forward to the wrong place if the settings were stale. The design therefore needs a visible target and a way to update it. I used the comparison to explain why the desktop agent can present a simple connection workflow even though the forwarding path has several parts underneath.

### Review or Verification

**Verification focus.** Compare a proxy-based prototype with an outbound agent workflow and capture the UI implications.

### Week 3 Deliverable

A forwarding comparison and a proposed connection flow for the desktop user.

### What I Took Away

From a desktop user's perspective, requiring manual router setup would complicate even a small demonstration. The agent workflow could keep the local service configuration in one place and show whether the gateway connection is available. I wanted the screen to guide the user through those two responsibilities clearly. The research also helped me explain why an active gateway connection and a healthy local target still need separate checks.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
