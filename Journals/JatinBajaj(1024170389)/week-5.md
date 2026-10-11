# Week 5 Project Journal — Distributed Performance Testing Platform

## Sprint 1 — Cloud Tunnel gateway Design

### Objective for the Week

The objective of Week 5 was to design the Cloud Tunnel Agent responsible for establishing communication between the cloud gateway and the Local Load Agent, enabling cloud-generated requests to reach applications hosted on student machines.

### Cloud Tunnel Agent Design

- Analysed the communication requirements between the cloud gateway and the local agent.
- Explored the use of a persistent connection to avoid establishing a new connection for every forwarded request.
- Studied how a tunnel can carry request and response data between the cloud and the local environment.
- Planned a communication protocol for identifying connections and associating forwarded requests with their corresponding responses.
- Considered connection registration, data forwarding, disconnection handling, and reconnection requirements.

### Technical Considerations

- Use asynchronous networking to support concurrent request forwarding.
- Maintain correct request-response correlation when multiple requests are active.
- Handle partial data, connection closures, and transmission failures.
- Minimise unnecessary encoding and decoding overhead in the forwarding path.
- Define clear interfaces between the tunnel and the Local Load Agent.

### Week 5 Deliverables

- Initial design of the Cloud Tunnel Agent.
- Preliminary tunnel communication and data-forwarding workflow.
- Identification of connection-management requirements.
- Initial plan for integrating the tunnel with the local agent and cloud gateway.

### Individual Contribution

My primary contribution was **designing the Cloud Tunnel Agent and its communication workflow**. The focus was on persistent connectivity, request and response forwarding, connection management, and defining the interfaces required for integration with the Local Load Agent.

### Planned Next Steps

- Implement the initial tunnel connection and registration mechanism.
- Validate bidirectional data forwarding between the cloud and local components.
- Coordinate with the team to integrate the tunnel with the reverse-proxy-based Local Load Agent.

> **Note:** The journal records the design objectives for Week 5. Specific implementation outcomes should be documented only after they have been verified.
