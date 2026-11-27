# Week 12 Project Journal — LoadMeter

## Week 12 — Final Testing and Deployment

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Jatin Bajaj (1024170389) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 12 |

### Objective for the Week

Bring up the project stack, verify the end-to-end workflow, and document the final benchmark and deployment checks.

The final week focused on the documented deployment and testing of the demonstration stack. The setup includes the Spring Boot target service with MySQL, the gateway and its MongoDB persistence, and the JavaFX agent configured to forward traffic to the selected local application. Starting these pieces is only the first part of verification. The team also needs to confirm the source of the response, the state of the agent connection, and the result returned through the gateway.

The final report provides both functional and performance evidence. It documents a 100-request run with all attempts passing and a 10,000-attempt benchmark that includes timeouts. The smaller test checks the basic path, while the larger run reveals more about behavior under pressure. The report's configuration and execution screenshots show how a user sees those results. The deployment handoff brings that evidence together with an order for checking the installed modules, including which symptoms to investigate if readiness or compatibility differs on another setup.

### Individual Contribution

**Gateway deployment and final load results.** I focused on the deployment sequence for the local target and MySQL, the gateway and MongoDB, and then the JavaFX agent. The report documents a tunneled 10,000-attempt run with 9,128 passes and 872 timeouts over 15.05 seconds, reporting 664.41 attempts per second.

For deployment, I organized the readiness checks around what the user can actually test. The sample API should respond on its own before the agent connects. The gateway should accept the agent registration, and a small tunneled workload should complete before the benchmark is attempted. That sequence provides a direct way to identify which part failed if the full setup does not behave as expected.

In the deployment handoff, I separated preparation from the final benchmark. The sample target needs its database and a successful direct API check. The gateway needs MongoDB and its listeners available. The agent then needs to register and forward to the selected local endpoint. A small forwarded workload gives the team a quick way to verify the assembled stack before increasing the request volume.

The documented benchmark used the tunneled route and offers a useful record of how the finished project behaves under those conditions. The reported attempt rate shows that the asynchronous engine finished substantial work during the run. The timeout count shows that a share of the attempts did not complete successfully. I used both facts in the handoff because someone reviewing deployment readiness needs the observed limit as well as the headline metric. I also included the first diagnostic questions for a future run, starting with whether the target, tunnel registration, and result retrieval still behave as they did in the demonstration setup.

### Review or Verification

**Verification focus.** Use the recorded benchmark to explain the gateway's behavior under load and keep timeouts visible in the deployment handoff.

### Week 12 Deliverable

A final backend startup checklist and a benchmark summary with the observed failure count.

### What I Took Away

I treated the recorded benchmark as evidence of throughput and of a limit under the tested conditions. The gateway completed the job and classified the individual attempts, including the 872 timeouts. I kept those timeouts in the handoff because reporting only the attempt rate would hide a substantial part of the result. This week's deployment scope is the documented gateway, agent, and target setup for the project demonstration.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
