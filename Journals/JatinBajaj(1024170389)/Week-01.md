# Week 1 Project Journal — LoadMeter

## Week 1 — Project Proposal and Problem Definition

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Jatin Bajaj (1024170389) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 1 |

### Objective for the Week

Define who needs LoadMeter, what problem it solves, and how the team will evaluate a first release.

The proposal began with a familiar situation in software development. An API may run only on the developer's computer, yet the developer wants to understand what happens when multiple clients use it at the same time. A local script is easy to start, but its work competes with the service for resources. Running the generator elsewhere changes the shape of the problem because the local service still needs a route for receiving and returning traffic.

The first release was framed around three roles. A local agent would connect outward and forward traffic to the selected service. A gateway would accept the tunnel connection and run a defined HTTP workload through it. A sample target would give the team a service with understandable responses for the demonstration. The immediate measure of progress was a complete request and response through that arrangement. More ambitious ideas such as distributing work across multiple workers were useful for the proposal and stayed separate from the first demonstration.

### Individual Contribution

**Backend problem and project scope.** The proposal separates the local target from the process generating the load. I focused on how a gateway could reach a service bound to localhost through an outbound agent connection.

I started by drawing the two jobs a conventional local test puts on the same computer. One job generates the traffic and the other serves it. When both compete for CPU and network resources, it becomes harder to tell which one caused a slowdown. Moving the generator elsewhere helps separate those jobs, but it also creates the problem of reaching an application that only listens on the developer's machine. The outbound agent connection gave us a path worth exploring for that constraint.

I considered two boundaries in the proposal. The first is between the process that generates the requests and the application being measured. The second is between the machine where the generator runs and the developer's local network. Separating the workload generator addresses the first boundary. An outbound tunnel initiated by the agent provides a candidate solution for the second. Explaining these separately made the proposed gateway easier to justify.

I also needed a sensible way to divide backend responsibilities. The gateway can receive a test specification, keep track of the active agent, and decide when to dispatch work. The agent can read the developer's target settings and forward each incoming stream to the appropriate local port. The target can remain a regular HTTP application, which makes its behavior useful for the demonstration. I treated the first successful single request as the initial architecture milestone. Once that route exists, a larger request count adds a measurable workload instead of changing every part of the design at once.

### Review or Verification

**Verification focus.** Outline the agent, gateway, and target responsibilities and identify their main handoffs.

### Week 1 Deliverable

A backend architecture outline and a first-pass request flow.

### What I Took Away

The initial design question was whether a remote request could reach the chosen local service and return a response through the same setup. I kept the proposal centered on that round trip. Adaptive stress testing and worker coordination remained longer-term ideas until the team had a working forwarding path.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
