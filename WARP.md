# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview
AI‑Powered Interactive Storytelling backend built with Spring Boot 3.4.3 and Java 24. Exposes REST APIs to create/manage stories and play interactive sessions, with AI-generated chapters/choices via OpenAI or Claude.

## Essential Commands

### Build and run (Mac/Linux vs Windows)
- Mac/Linux
  - Build: `./mvnw clean package`
  - Run: `./mvnw spring-boot:run`
  - Skip tests: `./mvnw clean package -DskipTests`
- Windows
  - Build: `.\mvnw clean package`
  - Run: `.\mvnw spring-boot:run`
  - Skip tests: `.\mvnw clean package -DskipTests`

### Testing
- All tests: `./mvnw test`
- Single class: `./mvnw test -Dtest=ClassName`
- Single method: `./mvnw test -Dtest=ClassName#methodName`

### Code quality
- Check format: `./mvnw spotless:check`
- Auto-format: `./mvnw spotless:apply`
- Verify (includes Spotless check): `./mvnw verify`

### Database and migrations
- Start Postgres (Compose v2): `docker compose up -d postgres` (or `docker-compose up -d postgres`)
- Stop stack: `docker compose down`
- Flyway status: `./mvnw flyway:info`
- Flyway migrate: `./mvnw flyway:migrate`

### Run via Docker
- Build + run: `docker compose up --build`
- Detached: `docker compose up -d`
- App logs: `docker compose logs -f app`
- Ports: local JVM runs on 8080; Docker maps container 8080 to host 8081 (see docker-compose.yml).

## Environment & configuration
- Copy `.env.example` to `.env` and fill required values. The app loads `.env` automatically via spring-dotenv.
- Minimum variables used by `application.properties`:
  - DB_URL, DB_USERNAME, DB_PASSWORD, SERVER_PORT (default 8080)
  - AI_PROVIDER (`openai` or `claude`)
  - OPENAI_API_KEY, OPENAI_MODEL (default gpt-4), OPENAI_MAX_TOKENS, OPENAI_TEMPERATURE
  - CLAUDE_API_KEY, CLAUDE_MODEL, CLAUDE_MAX_TOKENS, CLAUDE_TEMPERATURE
  - JWT_SECRET, JWT_EXPIRATION

API docs and health:
- Local run: http://localhost:8080/swagger-ui.html, http://localhost:8080/api-docs, http://localhost:8080/actuator/health
- Docker run: http://localhost:8081/swagger-ui.html, http://localhost:8081/api-docs, http://localhost:8081/actuator/health

## High-level architecture

### Layered design
- Controllers (`controller/`): thin REST endpoints returning DTOs (e.g., StoryController, StorySessionController, AIGenerationController, ChapterController).
- Services (`service/`): business logic, transactional operations (StoryService, StorySessionService, ChapterService, Auth/User services). AI strategy implemented here.
- Repositories (`repository/`): Spring Data JPA abstractions for entities.
- Entities (`entity/`): JPA models (Story, StoryChapter, StorySession, User, UserChoice, AICache, etc.).
- DTOs (`DTO/`): API request/response models.
- Config (`config/`): SecurityConfig, OpenAPIConfig, AIConfiguration/AIProviderConfig, CacheCleanupScheduler, JwtUtil.

### AI provider strategy
- `AIStoryGenerator` is the abstraction.
- `OpenAIStoryGenerator` and `ClaudeStoryGenerator` are conditionally loaded based on `ai.provider`.
- Provider credentials/models pulled from `ai.openai.*` and `ai.claude.*` properties.
- `AICacheService` stores generated content; expiration and cleanup are controlled by properties and a scheduled job (`CacheCleanupScheduler`).

### Security
- `SecurityConfig` currently permits all requests; CSRF disabled. JWT utilities exist (`JwtUtil`) but auth is not enforced yet.

### Persistence and migrations
- Flyway migrations live in `src/main/resources/db/migration` and run on startup.
- Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update` and SQL logging enabled for development.

## API surface
- All endpoints are under `/api/v1/`.
- Key areas: stories, chapters, sessions, AI generation.

## Important notes
- Requires JDK 24 (see `pom.xml`: `<java.version>24</java.version>` and compiler plugin).
- Spring Boot 3.4.3.
- When running with Docker Compose, the app is exposed on localhost:8081; when running via Maven, it uses localhost:8080.
- Flyway `validate-on-migrate=false`; review migrations before promoting to higher environments.
- Spotless enforces Google Java Format during `verify`.
