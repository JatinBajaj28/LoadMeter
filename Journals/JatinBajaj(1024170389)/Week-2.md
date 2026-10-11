# Week 2 Project Journal — Distributed Performance Testing Platform

## Sprint Planning & Initial Technical Research                                                    

## Objective for the Week

The objective of Week 2 was to initiate Sprint 1 planning, establish team development conventions, break down the proposed backend requirements into manageable tasks, and explore the feasibility of generating concurrent HTTP requests asynchronously.

## Day 1 — Sprint Planning & Repository Setup

- Participated in the Sprint 1 planning meeting to identify initial priorities following proposal submission.
- Discussed repository organisation, branch naming conventions, and basic collaboration guidelines to support parallel development.

## Day 2 — Architecture Review & Task Breakdown

- Broke down the proposed backend requirements into preliminary tasks related to the central controller and load-generator worker.
- Reviewed the expected communication flow between the controller and worker nodes, identifying responsibilities that would require further clarification.

## Day 3 — Technical Spike: Asynchronous HTTP Requests

- Conducted a small exploratory technical spike to investigate asynchronous HTTP request generation.
- Tested concurrent GET requests against a local dummy server to examine basic response handling and connection behaviour.
- Recorded observations from the experiment to inform the future design of the load-generator worker.

## Day 4 — Exploring Worker Configuration

- Researched how test settings could eventually be passed from the controller to a worker.
- Identified initial workload parameters, including the target URL, virtual-user count, request duration, and request rate, for further consideration.

## Day 5 — Controller–Worker Interface Discussion

- Discussed with Krish and Rishabh how test configurations could be represented in a simple JSON structure.
- Identified preliminary execution information that workers may need to record, including request outcomes, response status, and timing information.
- Noted the need for a consistent interface between workload configuration, request execution, and subsequent metrics processing.

## Day 6 — Technical Documentation & Team Review

- Documented the findings and outstanding questions from the asynchronous HTTP request experiment.
- Participated in the weekly team review to discuss progress, clarify unresolved design decisions, and identify priorities for the next iteration.

## Week 2 Deliverables

- Preliminary Sprint 1 task breakdown for the controller and worker modules.
- Initial findings from the exploratory asynchronous HTTP request experiment.
- Draft list of workload configuration parameters.
- Preliminary discussion of the JSON-based controller–worker interface.
- Initial notes on request-level execution information required for future metrics collection.

## Individual Contribution

My primary contribution was **backend planning and exploratory technical research**. I focused on breaking down the proposed controller and worker responsibilities, establishing repository collaboration conventions, investigating asynchronous HTTP request generation, and contributing to the preliminary design of workload configuration and worker execution data.

## Planned Next Steps

- Refine the proposed controller–worker interface.
- Clarify the minimum workload configuration required for the first test-execution milestone.
- Use the findings from the initial technical spike to guide the worker's design and subsequent validation.

> **Note:** Week 2 focused on planning, interface discussions, and a limited technical experiment. The experiment was exploratory and should not be interpreted as completion of the production load generator or the wider distributed platform.
