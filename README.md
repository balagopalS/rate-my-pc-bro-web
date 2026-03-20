# Rate My PC Bro 🖥️🔥

The ultimate AI-powered PC rating engine. No databases, no persistent storage—just pure hardware discovery and local LLM-driven verdicts.

## 🚀 How it Works
1. **Hardware Discovery:** Uses [OSHI](https://github.com/oshi/oshi) to read your local CPU, GPU, and RAM in real-time.
2. **AI Analysis:** Leverages [Spring AI](https://spring.io/projects/spring-ai) connected to a local **Ollama** instance.
3. **Stateless Verdict:** Delivers a JSON-formatted roast or performance prediction directly to your browser.

## 🛠️ Tech Stack
- **Java 21**
- **Spring Boot 3.4.0**
- **Spring AI (Ollama)**
- **OSHI** (Operating System and Hardware Information)
- **Maven**

## 🏁 Getting Started

### Prerequisites
1. **JDK 21+**
2. **Ollama:** Install and run [Ollama](https://ollama.com/) locally.
3. **Model:** Pull the llama3 model:
   ```bash
   ollama pull llama3
   ```

### Running the API
```bash
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8080/api`.

## 📡 API Modus Operandi

### 1. General Verdict
Returns a professional (and likely funny) verdict on your current PC hardware.
- **Endpoint:** `GET /api/ratemypcbro`
- **Response:**
  ```json
  {
    "rating": "8/10",
    "verdict": "A solid mid-range build.",
    "roast": "Your GPU is working harder than a freelance dev on rent day."
  }
  ```

### 2. Software Run Score
Predicts how a specific piece of software will perform on your machine.
- **Endpoint:** `GET /api/ratemypcbro/{typeofsoftware}/{name}`
- **Example:** `GET /api/ratemypcbro/game/cyberpunk2077`
- **Response:**
  ```json
  {
    "software": "cyberpunk2077",
    "score": "95/100",
    "verdict": "Ultra settings ready.",
    "performance_notes": "Expect 60+ FPS at 1440p."
  }
  ```

---
*Built with ❤️, powered by Local AI.*
