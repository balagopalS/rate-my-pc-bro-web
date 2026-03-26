# Rate My PC Bro API

An advanced, AI-powered hardware analysis and performance prediction engine built with Spring Boot.

## Overview

Rate My PC Bro is a stateless API designed to provide automated hardware appraisals and software performance predictions. By integrating real-time hardware discovery with Large Language Models (LLMs), the system delivers qualitative and quantitative verdicts on system configurations without the need for persistent data storage.

## Request Lifecycle

The API follows a structured request-response lifecycle to ensure accurate and context-aware verdicts:

1.  **Request Reception:** The API receives a GET request at one of the hardware analysis endpoints.
2.  **Hardware Discovery:** The **OSHI** service performs a deep inspection of the host system's CPU, GPU, and RAM.
3.  **Provider Selection:** The **AI Orchestrator** identifies the active inference engine (Local Ollama or External Proxy).
4.  **Prompt Engineering:** The system constructs a structured prompt containing the discovered hardware specifications and specific software requirements (if applicable).
5.  **Agentic RAG & Inference:**
    *   The AI model analyzes the prompt.
    *   If real-time data is required (e.g., latest market prices or 2026 benchmarks), the model invokes a registered **Search Tool** via Spring AI Function Calling.
    *   The model synthesizes a final verdict grounded in both static knowledge and retrieved internet data.
6.  **Structured Synthesis:** The model's response is automatically mapped to strictly typed DTOs (`GeneralVerdict`, `SoftwareVerdict`) using Spring AI's structured output features.
7.  **Response Delivery:** The final serialized JSON object is returned to the user.

## Architectural Principles

### 1. Statelessness
The application operates entirely in memory. It does not utilize a database; hardware specifications are captured per request, processed via AI, and returned as a transient response.

### 2. Automated Hardware Discovery
The system leverages the **OSHI (Operating System and Hardware Information)** library to perform deep inspection of the host machine's architecture, including CPU topology, GPU specifications, and memory configuration.

### 3. Agentic RAG & Internet Grounding
The architecture employs an **Agentic Retrieval-Augmented Generation (RAG)** pattern. Unlike traditional RAG that relies on a static vector store, this system allows the LLM to autonomously decide when to "search the internet" to fetch the latest benchmarks, hardware news, or software requirements. This is implemented via **Spring AI Function Calling**, which bridges the LLM with a real-time web search tool.

### 4. AI Orchestration
The API features a decoupled AI orchestration layer that allows for runtime switching between local and remote processing:
- **Local Provider:** Utilizes **Spring AI** and **Ollama** for private, on-device inference.
- **Proxy Provider:** Routes requests through an external API proxy, enabling advanced capabilities such as deep-web scraping and complex RAG workflows.

## Technology Stack

- **Java 21** (LTS)
- **Spring Boot 3.5.13**
- **Spring AI (1.0.0-M5)**
- **SpringDoc OpenAPI (Swagger)**
- **OSHI core**
- **Maven**

## Getting Started

### Prerequisites

1. **JDK 21** or higher.
2. **Ollama** (for local AI inference).
3. **Llama3.1 Model:** Ensure a tool-capable model is available locally:
   ```bash
   ollama pull llama3.1
   ```

### Execution

Compile and run the application using the Maven wrapper:
```bash
./mvnw spring-boot:run
```

The service defaults to `http://localhost:8081/api`.
(Port 8081 is used to avoid local conflicts).

### API Documentation (Swagger)
Interactive documentation is available at runtime:
- **URL:** `http://localhost:8081/api/swagger-ui/index.html`

## API Reference

### Hardware Analysis

#### General Hardware Verdict
Analyzes current system specifications and returns a comprehensive rating and critique.
- **URL:** `GET /ratemypcbro`
- **Response Format:** JSON

#### Software Performance Prediction
Predicts performance metrics for a specific application based on current hardware.
- **URL:** `GET /ratemypcbro/{type}/{name}`
- **Example:** `GET /ratemypcbro/game/cyberpunk2077`
- **Response Format:** JSON

### System Configuration

#### Update AI Provider
Toggles the active AI inference engine at runtime.
- **URL:** `POST /ratemypcbro/config/provider?type={LOCAL|PROXY}`
- **Response Format:** JSON

#### Current AI Provider Status
Retrieves the identity of the active inference engine.
- **URL:** `GET /ratemypcbro/config/provider`
- **Response Format:** JSON
