# Rate My PC Bro 🖥️🔥

The ultimate API for getting your rig judged. This is the backend for "Rate My PC Bro", a platform where users can submit their PC specs and get roasted or toasted by the community.

## 🚀 Tech Stack (2026 Edition)
- **Java 21** (LTS)
- **Spring Boot 3.4.0**
- **Maven** (Build System)
- **H2 Database** (In-memory for now, easy to swap)
- **Lombok** (Boilerplate reduction)
- **Jakarta Validation** (Keeping those specs clean)

## 🛠️ Getting Started

### Prerequisites
- JDK 21+
- Maven 3.9+

### Running the App
```bash
./mvnw spring-boot:run
```
(Or use your IDE's run button for `RateMyPcBroApplication.java`).

The API will be available at `http://localhost:8080/api`.

### Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/ping` | Health check to see if the bro is alive. |

## 🧪 Database & Console
- **H2 Console:** `http://localhost:8080/api/h2-console`
- **JDBC URL:** `jdbc:h2:mem:rate-my-pc-bro-db`
- **Credentials:** `sa` / `password`

## 🤝 Contributing
Just open a PR, bro. Let's make this the best PC rating platform ever.

---
*Built with ❤️ (and too much RGB).*
