# Week 12 Project Journal — LoadMeter

## Week 12 — Final Testing and Deployment

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Rishabh Dutt (1024170393) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 12 |

### Objective for the Week

Bring up the project stack, verify the end-to-end workflow, and document the final benchmark and deployment checks.

The final week focused on the documented deployment and testing of the demonstration stack. The setup includes the Spring Boot target service with MySQL, the gateway and its MongoDB persistence, and the JavaFX agent configured to forward traffic to the selected local application. Starting these pieces is only the first part of verification. The team also needs to confirm the source of the response, the state of the agent connection, and the result returned through the gateway.

The final report provides both functional and performance evidence. It documents a 100-request run with all attempts passing and a 10,000-attempt benchmark that includes timeouts. The smaller test checks the basic path, while the larger run reveals more about behavior under pressure. The report's configuration and execution screenshots show how a user sees those results. The deployment handoff brings that evidence together with an order for checking the installed modules, including which symptoms to investigate if readiness or compatibility differs on another setup.

### Individual Contribution

**Desktop deployment smoke test and final demo.** I focused on the desktop smoke-test sequence for the deployed project stack: connect to the gateway, submit a test, watch it finish, and inspect the dashboard. The final report shows the configuration view, execution KPIs, charts, detailed requests, and history view.

I arranged the final desktop check in the order a new user would follow after deployment. Confirm the selected local host and port, wait for the connection status, submit a known workload, and confirm the monitor shows the outcome for that same test. Once the run finishes, check that the summary and detailed request view make it possible to explain at least one success or failure.

I structured the desktop deployment check around the user's full path. Start by confirming the selected host and port and observing the connection status after registration. Submit a short workload with known settings. Follow the job in the execution monitor and compare the summary with the individual outcomes. This provides a practical way to confirm the interface and deployment together rather than reviewing static screens in isolation.

For the final demonstration notes, I explained what each view contributes to understanding the documented benchmark. The configuration screen records the intended input. The outcome and latency views describe what happened during the run. The detail table offers examples the user can inspect. I kept the history and export checks tied to the test identifier and the chosen build, because those actions use saved results and depend on the corresponding gateway behavior. The resulting handoff describes what to repeat after deployment and how to report a discrepancy if a view, selected result, or exported file differs from expectations.

### Review or Verification

**Verification focus.** Use the demonstration build to check that history, request pagination, and CSV export work with its gateway routes before handing over the demo.

### Week 12 Deliverable

A desktop deployment smoke checklist and a final walkthrough tied to the report screenshots.

### What I Took Away

The report captures the main screens used for that walkthrough. I used the screenshots to describe where a user can see throughput, latency, request outcomes, and previous runs. For the deployment handoff, I kept history, page navigation, and CSV export as explicit checks on the exact client and gateway build used for the demo. This gives the next tester a practical sequence to repeat after startup.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
