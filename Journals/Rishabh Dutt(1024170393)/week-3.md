# Week 3 Project Journal — Distributed Performance Testing Platform

## Research on Nginx and Network Traffic Management

### Objective for the Week

The objective of Week 3 was to explore Nginx as a reverse proxy and traffic-management solution, understand its routing and load-balancing capabilities, and determine whether it could address the networking requirements of LoadMeter.

### Day 1 — Introduction to Nginx

- Studied Nginx and its use as a web server, reverse proxy, and load balancer.
- Explored its role in handling incoming network connections and forwarding requests to backend services.
- Identified potential applications of Nginx in a cloud-based system architecture.

### Day 2 — Reverse Proxy and Request Routing

- Studied how a reverse proxy receives requests and forwards them to an appropriate backend.
- Explored basic request-routing concepts and upstream server configuration.
- Examined how a proxy can provide a consistent entry point for backend services.

### Day 3 — Load Balancing and Connection Handling

- Explored how Nginx can distribute incoming traffic across multiple backend servers.
- Studied its connection-handling capabilities and the role of upstream configurations.
- Examined how a reverse proxy can support the deployment of scalable backend infrastructure.

### Day 4 — Nginx and Load-Testing Requirements

- Investigated the distinction between routing traffic and generating application-level load.
- Identified that Nginx alone does not provide LoadMeter's required test planning, virtual-user scheduling, and performance metrics.
- Examined why remote cloud workers still require a mechanism to access an application hosted on a student's localhost.

### Day 5 — Architecture Evaluation and Decision

- Evaluated whether Nginx should be a mandatory component of the initial LoadMeter architecture.
- Identified potential future uses, including TLS termination, edge routing, and traffic distribution.
- Concluded that the initial implementation should prioritise the gateway, cloud load generator, and lightweight local agent instead of introducing an additional proxy layer prematurely.

### Day 6 — Documentation and Weekly Review

- Documented the findings from the Nginx exploration.
- Summarised its advantages and limitations in relation to LoadMeter's requirements.
- Presented the findings for comparison with the load-testing tools studied by the other team members.

### Week 3 Deliverables

- Research notes on Nginx's reverse-proxy and load-balancing capabilities.
- Preliminary evaluation of Nginx as a supporting infrastructure component.
- Identification of the distinction between traffic routing and load generation.
- Recommendation to defer Nginx integration until a clear deployment requirement emerges.

### Individual Contribution

My primary contribution was **researching Nginx and evaluating its role in LoadMeter's network architecture**. I focused on reverse proxying, request routing, load balancing, and connection handling, and assessed whether Nginx could solve the platform's core requirement of connecting cloud-based load generation to locally hosted applications.

### Planned Next Steps

- Continue with the implementation of the gateway-to-agent communication path.
- Evaluate whether a dedicated reverse proxy is necessary for the first deployment.
- Investigate TLS, connection management, and secure request forwarding requirements for future deployment stages.

**Reference:** https://nginx.org/en/docs/
