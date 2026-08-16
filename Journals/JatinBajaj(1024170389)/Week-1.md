# Week 1 Project Journal — Distributed Performance Testing Platform

## Week 1 — Ideation, Research & Project Proposal

| Field | Details |
|---|---|
| Course | UCS503 / UCS503P — Software Engineering |
| Institute | Thapar Institute of Engineering and Technology, Patiala |
| Project | Distributed Performance Testing Platform |
| Member | Jatin Bajaj — 1024170389 |
| Team Members | Jatin Bajaj — 1024170389, Krish Sahni — 1024170390, Rishabh Dutt — 1024170393 |
| Project Stage | Project Proposal |
| Week | Week 1 |

## Objective for the Week

The objective of Week 1 was to understand the load-testing problem, research existing approaches, define the proposed system architecture and features, and prepare the project proposal.

## Day 1 — Problem Understanding

- Analysed the problem of generating thousands of concurrent requests from a low-resource machine.\n- Identified why running the load generator and the application on the same machine can create a bottleneck.

## Day 2 — Existing Systems & Research

- Studied JMeter's local and distributed load-testing approach.\n- Analysed the Controller--Worker model and how multiple machines can increase load-generation capacity.

## Day 3 — Proposed Architecture / Technical Research

- Designed the proposed Controller--Worker architecture.\n- Defined the roles of the central controller, cloud workers, local agent, communication gateway, metrics pipeline, and storage.\n- Studied the basic localhost testing flow.

## Day 4 — Feature & Technical Planning

- Proposed the lightweight local agent for connecting cloud workers to applications running on `localhost`.\n- Defined workload distribution, worker registration, health checks, secure communication, and scalability as future backend requirements.

## Day 5 — Proposal Development

- Worked mainly on the technical architecture and backend requirements of the proposal.\n- Defined asynchronous request generation, telemetry flow, fault-tolerance, and horizontal scaling requirements.

## Day 6 — Proposal Finalisation

- Reviewed and refined the technical sections of the proposal.\n- Finalised the proposed architecture, workflow, scope, and major backend components with the team.

## Week 1 Deliverables

- Finalised the project problem and objective.
- Studied existing load-testing approaches.
- Defined the proposed distributed architecture.
- Identified major system components and proposed future capabilities.
- Defined initial performance metrics and technical requirements.
- Prepared and refined the project proposal.

## Individual Contribution

My primary contribution was the **core backend and distributed-system design**. I focused on the Controller--Worker architecture, cloud load generators, local-agent concept, localhost connectivity, workload distribution, worker coordination, scalability, secure communication, and technical planning for the proposed system.

> **Note:** The features discussed during Week 1, such as adaptive stress testing, performance regression testing, CI/CD integration, and real-time visualization, are proposed future capabilities of the platform and were not implemented during Week 1.
