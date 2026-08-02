# Spring AI & Agentic RAG Development Guidelines

This document captures the architectural standards, testing invariants, and configuration rules for the **Rate My PC Bro** engine.

---

## 1. Integration Test Self-Healing & Dual-Provider Readiness
- **Automated Health Check & Auto-Start**: Integration tests depending on local services (e.g., Ollama on `http://localhost:11434`) must execute a `@BeforeAll` health ping and auto-launch `ollama serve` via `ProcessBuilder` if offline.
- **Fail-Fast Readiness Assertions**: Tests requiring dual inference modes (LOCAL + PROXY) must explicitly assert pre-requisite readiness (e.g., non-empty `OPENROUTER_API_KEY`, local model availability) at startup to provide clear diagnostics instead of cryptic runtime errors.

---

## 2. Spring AI `BeanOutputConverter` Schema Invariant
- When using `.entity(Class)` with Spring AI's structured output converter, system instructions must explicitly include:
  ```text
  CRITICAL: Return ONLY a raw JSON instance object with key-value data fields.
  Do NOT include "$schema", "type", or "properties" wrappers.
  ```
  *Rationale*: Prevents models (e.g., `openai/gpt-4o-mini`) from echoing the JSON schema wrapper structure itself.

---

## 3. OpenRouter Base URL Mapping
- When configuring OpenRouter for Spring AI's OpenAI starter (`spring.ai.openai`), specify:
  ```yaml
  spring:
    ai:
      openai:
        base-url: https://openrouter.ai/api
  ```
  *Rationale*: Spring AI automatically appends `/v1/chat/completions`. Setting `base-url` to `https://openrouter.ai/api` ensures requests resolve to `https://openrouter.ai/api/v1/chat/completions` without double `/v1/v1` 404 errors.
