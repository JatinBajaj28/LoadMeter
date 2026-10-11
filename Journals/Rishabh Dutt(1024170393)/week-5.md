# Week 5 Project Journal — Distributed Performance Testing Platform

## Sprint 1 — Phase 1 Local Load Agent Design

### Objective for the Week

The objective of Week 5 was to begin the design of Phase 1 of the Local Load Agent, focusing on a reverse-proxy-based approach for forwarding load-testing requests to applications running on the student's local machine.

### Local Load Agent Design

- Analysed the role of the Local Load Agent in connecting the cloud-based load-testing platform with a locally hosted application.
- Explored the reverse proxy concept and how incoming requests can be forwarded to a configured local target.
- Identified the basic responsibilities of the agent, including request forwarding, response handling, connection management, and error handling.
- Considered how the agent should communicate with the cloud-side components without requiring the student's application to be directly exposed to the internet.
- Planned the initial component structure and request-processing workflow for Phase 1.

### Technical Considerations

- Keep the agent lightweight and minimise unnecessary processing overhead.
- Restrict forwarding to explicitly configured local application targets.
- Preserve request methods, headers, and bodies wherever applicable.
- Handle connection failures and timeouts appropriately.
- Keep the design modular so that tunnel communication can be integrated with the agent.

### Week 5 Deliverables

- Initial design of Phase 1 of the Local Load Agent.
- Preliminary reverse-proxy request-forwarding workflow.
- Identification of core components and responsibilities.
- Initial technical considerations for local application connectivity.

### Individual Contribution

My primary contribution was **designing Phase 1 of the Local Load Agent using a reverse-proxy-based approach**. The focus was on defining how requests would reach the local application, how responses would be handled, and how the agent would fit into the overall LoadMeter architecture.

### Planned Next Steps

- Implement the initial request-forwarding functionality.
- Validate requests against a locally hosted test application.
- Coordinate the interface between the Local Load Agent and Cloud Tunnel Agent.

> **Note:** This journal describes the planned design work for Week 5. Implementation and testing results should be added according to the work actually completed.
