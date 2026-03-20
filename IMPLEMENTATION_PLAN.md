# Implementation Plan: Agentic RAG Hardware Analysis

This document outlines the technical phases required to transition the "Rate My PC Bro" API from a static prompt architecture to a dynamic, internet-grounded Agentic RAG system.

## Phase 1: Search Tool Integration (Foundational)

The goal is to provide the LLM with "eyes" on the 2026 hardware market.

1.  **Define Search Request/Response Models:**
    *   Create `SearchRequest` and `SearchResponse` records to standardize data exchange.
2.  **Implement `WebSearchTool`:**
    *   Develop a Spring-managed service that interfaces with a search provider (e.g., Brave Search API, Tavily, or a custom scraper).
    *   Annotate with `@Bean` and a precise `@Description` to enable Spring AI auto-discovery for function calling.
3.  **Security & Rate Limiting:**
    *   Implement basic caching for search results (stateless within a time window) to prevent redundant API calls.

## Phase 2: Agentic Orchestration (Core)

Refactoring the AI providers to support multi-step reasoning.

1.  **Enhance `OllamaAiProvider`:**
    *   Migrate to the `ChatClient` fluent API.
    *   Register the `ToolCallAdvisor` to enable the model to autonomously invoke the `WebSearchTool`.
    *   Switch to a tool-capable model (e.g., `llama3.1`).
2.  **Prompt Engineering Refinement:**
    *   Develop a system prompt that instructs the model to "Always verify 2026 benchmarks and pricing using the search tool before issuing a verdict."
    *   Enforce structured output requirements (JSON) as a hard constraint in the prompt.

## Phase 3: Structured Data Synthesis (Output)

Ensuring the AI's creative "roasts" don't break the API contract.

1.  **Define Output DTOs:**
    *   `GeneralVerdictResponse` and `SoftwarePerformanceResponse` models.
2.  **Implement `BeanOutputConverter`:**
    *   Utilize Spring AI's output converters to map raw LLM strings directly into Java objects, providing a layer of validation before the controller responds.

## Phase 4: Proxy & RAG Alignment (Advanced)

Ensuring parity between local and remote providers.

1.  **Proxy Capabilities Extension:**
    *   Update `ProxyAiProvider` to support passing tool definitions or instructing the remote proxy to utilize its own RAG/Search capabilities.
2.  **Connectivity Handling:**
    *   Implement fallback logic if the search tool or proxy is unavailable (e.g., falling back to the model's internal training data with a disclaimer).

## Phase 5: Verification & Validation (Final)

1.  **Automated Integration Tests:**
    *   Mock the `WebSearchTool` to verify that the LLM successfully "calls" the function when presented with unknown hardware.
2.  **Manual "2026 Drift" Test:**
    *   Query the API for a hypothetical hardware component released after the model's cutoff to ensure it successfully retrieves internet data.
3.  **Performance Benchmarking:**
    *   Measure the latency added by the multi-step search process and optimize if necessary.
