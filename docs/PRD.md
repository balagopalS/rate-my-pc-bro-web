# Product Requirements Document (PRD)

## Product Overview

Rate My PC Bro is an automated hardware appraisal and software compatibility analysis engine. The application combines host hardware discovery with Large Language Models (LLMs) and real-time internet retrieval to provide qualitative hardware evaluations and quantitative software performance predictions.

---

## Core Problem Statement

Users require immediate, data-driven assessments of their system hardware capabilities relative to modern application requirements and industry benchmarks without relying on manual entry or outdated static databases.

---

## Jobs to Be Done (JTBD)

When evaluating system hardware or considering software purchases, users require an automated evaluation of their host configuration so that they can determine performance viability and identify potential hardware bottlenecks.

---

## Desirability, Viability, and Feasibility (DVF)

- **Desirability**: Provides instant system evaluation and software performance predictions based on real-time hardware discovery.
- **Viability**: Operates statelessly without persistent storage requirements, utilizing customizable and cost-effective AI provider runtimes (`LOCAL` and `PROXY`).
- **Feasibility**: Built on Spring Boot, OSHI hardware discovery, and Spring AI function calling interfaces.

---

## Architectural Moat

An Agentic Retrieval-Augmented Generation (RAG) pipeline that combines local hardware discovery via OSHI with web retrieval through Tavily AI Search and structured DTO response guarantees via Spring AI output converters.

---

## Non-Functional Requirements

1. **Stateless Execution**: The application must process all requests in-memory without persistent database dependencies.
2. **Thread Safety**: Request-scoped tool execution tracking must be strictly isolated per thread using `ThreadLocal` storage.
3. **Structured Response Contracts**: All LLM outputs must map strictly to typed Java DTOs (`GeneralVerdict`, `SoftwareVerdict`).
