# Implementation Plan: Agentic RAG Hardware Analysis Engine

Technical phase roadmap for the Rate My PC Bro engine.

---

## Phase 1 — Structured Output & Contract Safety (Completed)
- [x] **Structured DTO Output**: Enforce contract safety using Spring AI `BeanOutputConverter` for `GeneralVerdict` and `SoftwareVerdict`.
- [x] **Prompt Hardening**: Standardize JSON anti-preamble formatting instructions in `instructions.json`.

---

## Phase 2 — Retrieval Augmentation & Web Search (Completed)
- [x] **Web Search Integration**: Integrate Tavily AI Search API for live benchmark and compatibility lookups.
- [x] **Caching Layer**: Implement query snippet caching in `WebScraper` to minimize redundant external API calls.

---

## Phase 3 — Agentic Tool Orchestration & Tracing (Completed)
- [x] **Discrete Tool Suites**: Register function-calling tool beans for general PC verdicts and software-specific appraisals.
- [x] **Execution Tracing**: Implement `ToolCallContext` thread-local storage to record tool execution name, query input, latency, and snippet traces in API responses.

---

## Phase 4 — Interactive Flow & Session Context (Planned)
- [ ] **Interactive Conversational Endpoint**: Implement multi-turn conversational API supporting follow-up hardware and performance queries.
- [ ] **Chat Memory Advisor Integration**: Integrate Spring AI `MessageChatMemoryAdvisor` with session ID partitioning.

---

## Phase 5 — Developer & Web Interface (Planned)
- [ ] **Web Dashboard**: Provide a visual web dashboard displaying hardware metrics, performance scores, and tool call timelines.
- [ ] **CLI Interface**: Add Spring Shell support for local terminal execution.
