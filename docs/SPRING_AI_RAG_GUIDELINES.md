# Spring AI & Agentic RAG Development Guidelines

This document outlines technical standards, schema constraints, and configuration rules for the Rate My PC Bro engine.

---

## 1. Integration Testing and Provider Self-Healing

- **Automated Health Verification**: Integration tests dependent on local services (such as Ollama on `http://localhost:11434`) should attempt automated health checks prior to execution.
- **Fail-Fast Readiness Assertions**: Dual-provider integration tests (`LOCAL` and `PROXY`) must assert prerequisite environment configurations (e.g., non-empty `OPENROUTER_API_KEY`, model availability) at startup to yield explicit failure diagnostics.

---

## 2. Spring AI Output Converter Schema Invariants

- System prompts used with Spring AI `BeanOutputConverter` or `.entity(Class)` must explicitly enforce raw JSON formatting:
  ```text
  CRITICAL: Return ONLY a raw JSON instance object with key-value data fields.
  Do NOT include "$schema", "type", or "properties" wrappers.
  ```
- *Rationale*: Guarantees that third-party proxy models (e.g., `openai/gpt-4o-mini`) return deserializable JSON payloads without JSON Schema wrapper envelope artifacts.

---

## 3. OpenRouter Proxy Configuration

- When configuring OpenRouter via Spring AI's OpenAI client (`spring.ai.openai`), set the base URL to:
  ```yaml
  spring:
    ai:
      openai:
        base-url: https://openrouter.ai/api
  ```
- *Rationale*: Spring AI automatically appends `/v1/chat/completions`. Specifying `https://openrouter.ai/api` ensures proper resolution to `https://openrouter.ai/api/v1/chat/completions`.

---

## 4. ThreadLocal Context Management

- Tool execution tracking is maintained per request thread using `ToolCallContext`.
- Controller endpoints must clean up `ThreadLocal` context within a `finally` block:
  ```java
  try {
      // Process request
  } finally {
      ToolCallContext.clear();
  }
  ```
