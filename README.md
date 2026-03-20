# Rate My PC Bro API

An advanced, AI-powered hardware analysis and performance prediction engine built with Spring Boot.

## Overview

Rate My PC Bro is a stateless API designed to provide automated hardware appraisals and software performance predictions. By integrating real-time hardware discovery with Large Language Models (LLMs), the system delivers qualitative and quantitative verdicts on system configurations without the need for persistent data storage.

## Architectural Principles

### 1. Statelessness
The application operates entirely in memory. It does not utilize a database; hardware specifications are captured per request, processed via AI, and returned as a transient response.

### 2. Automated Hardware Discovery
The system leverages the **OSHI (Operating System and Hardware Information)** library to perform deep inspection of the host machine's architecture, including CPU topology, GPU specifications, and memory configuration.

### 3. AI Orchestration
The API features a decoupled AI orchestration layer that allows for runtime switching between local and remote processing:
- **Local Provider:** Utilizes **Spring AI** and **Ollama** for private, on-device inference.
- **Proxy Provider:** Routes requests through an external API proxy, enabling advanced capabilities such as real-time internet scraping and Retrieval-Augmented Generation (RAG).

## Technology Stack

- **Java 21** (LTS)
- **Spring Boot 3.4.0**
- **Spring AI**
- **OSHI core**
- **Maven**

## Getting Started

### Prerequisites

1. **JDK 21** or higher.
2. **Ollama** (for local AI inference).
3. **Llama3 Model:** Ensure the model is available locally:
   ```bash
   ollama pull llama3
   ```

### Execution

Compile and run the application using the Maven wrapper:
```bash
./mvnw spring-boot:run
```

The service defaults to `http://localhost:8080/api`.

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
