# Rate My PC Bro — Project Instructions (GEMINI.md)

See [.geminirules](file:///C:/Users/balag/Desktop/Coding/rate-my-pc-bro-web/.geminirules) for full configuration details.

## Quick Summary
- **Stack**: Java 21, Spring Boot 3.5, Spring AI (1.0.0-M5), OSHI, Jsoup, OpenRouter / Ollama.
- **Inference Engines**: Toggleable runtime between `LOCAL` (Ollama) and `PROXY` (OpenRouter API).
- **Cache Management**: `POST /ratemypcbro/config/cache/clear`.
- **Testing**: Self-healing integration tests in `AiOrchestratorMultiProviderTest.java`.
