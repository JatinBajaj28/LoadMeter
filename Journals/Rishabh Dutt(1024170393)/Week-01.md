# Week 1 Project Journal — LoadMeter

## Week 1 — Project Proposal and Problem Definition

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Rishabh Dutt (1024170393) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 1 |

### Objective for the Week

Define who needs LoadMeter, what problem it solves, and how the team will evaluate a first release.

The proposal began with a familiar situation in software development. An API may run only on the developer's computer, yet the developer wants to understand what happens when multiple clients use it at the same time. A local script is easy to start, but its work competes with the service for resources. Running the generator elsewhere changes the shape of the problem because the local service still needs a route for receiving and returning traffic.

The first release was framed around three roles. A local agent would connect outward and forward traffic to the selected service. A gateway would accept the tunnel connection and run a defined HTTP workload through it. A sample target would give the team a service with understandable responses for the demonstration. The immediate measure of progress was a complete request and response through that arrangement. More ambitious ideas such as distributing work across multiple workers were useful for the proposal and stayed separate from the first demonstration.

### Individual Contribution

**User workflow and monitoring needs.** I mapped the developer's path from connecting a local service to configuring a test and reading the results. The proposal needed to explain what the tester sees at each stage.

I walked through the proposal from the point of view of someone trying to test an API on their own laptop. Before filling out a load-test form, they need to know which local service the agent will reach and whether the gateway can communicate with it. Once the test starts, they need a way to tell if the run is still active and where to find its results.

I began the interface outline with the developer's most immediate questions. Which local service is selected? Has the agent connected? Where do I enter the workload? Once the gateway accepts the test, the user's questions change. They now want to know whether it is still running, how many requests passed, and where to look if something went wrong.

Those questions helped me decide how to divide the presentation. The connection area should show what is configured and what the agent is doing. The workload area should describe what will be sent. The monitor should present the outcome and give the developer a path from an overall summary to request details. I considered using a previously saved result during a demonstration because it can let the team inspect the charts without waiting for another large run. For the proposal, I wrote the workflow in that order so the UI requirements followed how a user would actually approach the project.

### Review or Verification

**Verification focus.** List the connection, configuration, monitoring, and history screens as candidate features.

### Week 1 Deliverable

A workflow sketch and initial dashboard requirements.

### What I Took Away

That walkthrough helped me separate the first-release screens from features that could wait. Connection feedback, request settings, progress, and a readable summary belonged in the first version. Historical comparison and richer dashboard interactions could follow once the essential workflow was clear. I used those distinctions to make the proposal's scope easier to explain.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
