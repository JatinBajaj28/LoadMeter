# Week 12 Project Journal — LoadMeter

## Week 12 — Final Testing and Deployment

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | LoadMeter — Cloud Tunnel Gateway and Local Load Testing Platform |
| Member | Krish Sahni (1024170390) |
| Team Members | Jatin Bajaj (1024170389), Krish Sahni (1024170390), Rishabh Dutt (1024170393) |
| Week | 12 |

### Objective for the Week

Bring up the project stack, verify the end-to-end workflow, and document the final benchmark and deployment checks.

The final week focused on the documented deployment and testing of the demonstration stack. The setup includes the Spring Boot target service with MySQL, the gateway and its MongoDB persistence, and the JavaFX agent configured to forward traffic to the selected local application. Starting these pieces is only the first part of verification. The team also needs to confirm the source of the response, the state of the agent connection, and the result returned through the gateway.

The final report provides both functional and performance evidence. It documents a 100-request run with all attempts passing and a 10,000-attempt benchmark that includes timeouts. The smaller test checks the basic path, while the larger run reveals more about behavior under pressure. The report's configuration and execution screenshots show how a user sees those results. The deployment handoff brings that evidence together with an order for checking the installed modules, including which symptoms to investigate if readiness or compatibility differs on another setup.

### Individual Contribution

**Final regression and deployment verification.** I organized the final checks around the target API, gateway handshake, input validation, and smoke and stress runs. The report records P50 at 931 ms, P95 at 5,042 ms, and P99 at 5,099 ms alongside 872 timeouts in the 10,000-attempt run.

My final test sequence started with the direct sample API, then added the gateway and agent, then exercised the same service through a small tunneled run. The report records a 100-request run in which all requests passed. That run and the larger stress benchmark serve different purposes: the first checks the basic path, and the second records behavior under greater load.

I framed the final tests as a progression from known responses to a realistic workload. The target's CRUD and validation checks establish expected API behavior. A registered agent and a small test through the tunnel check the route and the expected response. The larger benchmark then helps evaluate throughput, the measured latency distribution, and failure classification with a larger set of attempts.

For the final summary, I wrote down what the evidence supports. The documented smoke run demonstrates a successful smaller workload. The stress run produced both passes and timeouts, and its percentile values describe the measurements recorded under that configuration. Those observations should travel with the deployment notes, especially if the team later changes timeouts, concurrency, or the target environment. I also distinguished evidence already in the project report from checks that belong to a future deployment or a peer feedback session. This makes it easier to hand over a verification sequence that someone else can repeat and compare.

### Review or Verification

**Verification focus.** Record results with their test configuration and distinguish the benchmark evidence from peer-feedback placeholders.

### Week 12 Deliverable

A final regression summary, deployment smoke checks, and a qualified interpretation of the benchmark.

### What I Took Away

I compared the latency figures with the failure count when summarizing deployment readiness. The tail values near five seconds and the recorded timeouts belong alongside the reported throughput. I also separated what the report actually captures from items that still require a team check, such as verifying the specific build installed for the demo or completing the peer feedback section. This keeps the handoff useful without turning a single benchmark into a claim about every deployment.

*Retrospective draft based on the project repository and final report. Week assignments and individual details are for team review.*
