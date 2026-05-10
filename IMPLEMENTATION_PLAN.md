# Implementation Plan: Contemporary Agentic RAG Hardware Analysis

This document outlines the technical phases required to transition the "Rate My PC Bro" API from a static prompt architecture to a dynamic, internet-grounded, production-grade Agentic system using Spring AI.

---

## PHASE 1 — Make Outputs Production-Grade (Highest Priority)
**Goal:** Stop returning vague text blobs; enforce contract-safety.
- [x] **Harden Structured Output:** Explicitly use Spring AI's `BeanOutputConverter` to guarantee model output strictly obeys JSON schemas.
- [x] **DTO Refinement:** Update `GeneralVerdict` and `SoftwareVerdict` records with validation annotations.
- [x] **Provider Migration:** Update `OllamaAiProvider` and others to explicitly enforce `.entity(Class)` validation chains and handle parsing fallbacks.

---

## PHASE 2 — Retrieval Augmentation (Crucial Step)
**Goal:** Stop relying purely on stale model memory; fetch real hardware data.
- [ ] **Data Gathering Pipeline:** Gather local PC specs -> Determine necessary queries -> Fetch live benchmark/pricing data.
- [ ] **Context Augmentation:** Before passing data to the LLM, inject the retrieved search snippets directly into the ChatPrompt to guarantee data accuracy.

---

## PHASE 3 — Tool Orchestration (Agentic Entry)
**Goal:** Let the model decide *when* and *what* tools to use.
- [ ] **Implement Web Tools:** Develop Spring-managed `@Bean` functions annotated with `@Description` (e.g., `searchWebForBenchmarks`, `checkHardwareCompatibility`).
- [ ] **Register Function Callers:** Register `ToolCallAdvisor` with ChatClients to allow autonomous dynamic tool triggering by the model.

---

## PHASE 4 — User Interface & CLI Access
**Goal:** Bridge the gap between backend logic and end-users with intuitive interfaces.
- [ ] **Modern Web Dashboard:** Build a beautiful, dark-themed Thymeleaf view (`index.html`) leveraging Vanilla CSS with dynamic visual performance gauges and sleek layout for AI text.
- [ ] **Interactive Spring Shell:** Implement a lightweight CLI controller enabling commands directly from developer terminal shells.

---

## PHASE 5 — Context + Memory (Conversational Continuity)
**Goal:** Enable logical follow-ups (e.g., "Now check 1440p with those specs").
- [ ] **Chat Memory Advisors:** Register `MessageChatMemoryAdvisor` using in-memory or persistent map stores.
- [ ] **Session ID Management:** Update controllers/orchestrators to capture `chatId` to correctly partition user history.
- [ ] **Dynamic Retrieval-Aware Assembly:** Combine current user message, historical window, and injected retrieved specs into a unified prompt context.

---

## PHASE 6 — Source Grounding & Citations
**Goal:** Establish trust and show exactly *why* conclusions were reached.
- [ ] **Expand Schema:** Add `List<Source>` field to verdict responses containing URLs, confidence scores, and text snippets used.
- [ ] **Tool Chain Metadata:** Capture source metadata from the functions executed in Phase 3 and explicitly marshal them into the API's JSON response.

---

## PHASE 7 — Lightweight Semantic Retrieval (Optional Expansion)
**Goal:** Use Embeddings for caching and fast local memory searches.
- [ ] **Vector Store Integration:** Add a local Vector DB (e.g., pgvector or Chroma) to cache repeated web lookup summaries.
- [ ] **Semantic Similarity Caching:** Check local vector memory for similar benchmark queries before firing external API calls to save credits and drastically cut latency.

---

## Verification Plan
- [ ] **Automated Tests:** Mock tool interfaces to ensure models generate autonomous execution plans.
- [ ] **UI/Manual Testing:** Verify dark mode style and animation rendering in browsers; Verify Shell prompt interactivity.
- [ ] **Grounding Audit:** Submit API requests and assert the response JSON includes valid citation links.
