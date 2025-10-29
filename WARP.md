# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

AI-Powered Interactive Storytelling Backend built with Spring Boot 3.4.3 and Java 24. The application provides REST APIs for creating, managing, and playing interactive stories with AI-generated content using OpenAI or Claude.

## Essential Commands

### Build and Run
```powershell
# Build the project (compiles code, runs tests)
.\mvnw clean package

# Run the application locally
.\mvnw spring-boot:run

# Build without tests
.\mvnw clean package -DskipTests
```

### Code Quality
```powershell
# Check code formatting (Google Java Style)
.\mvnw spotless:check

# Apply code formatting automatically
.\mvnw spotless:apply

# Verify build with formatting check
.\mvnw verify
```

### Testing
```powershell
# Run all tests
.\mvnw test

# Run specific test class
.\mvnw test -Dtest=ClassName

# Run specific test method
.\mvnw test -Dtest=ClassName#methodName
```

### Database
```powershell
# Start PostgreSQL via Docker Compose
docker-compose up -d postgres

# Stop and remove containers
docker-compose down

# View Flyway migration status
.\mvnw flyway:info

# Run Flyway migrations manually
.\mvnw flyway:migrate
```

### Docker
```powershell
# Build and run full stack
docker-compose up --build

# Run in detached mode
docker-compose up -d

# View logs
docker-compose logs -f app
```

## Architecture

### Layered Architecture

The codebase follows a standard Spring Boot layered architecture pattern:

**Controller Layer** (`controller/`)
- REST API endpoints using `@RestController`
- Request/response handling with DTOs
- Thin controllers - business logic delegated to services
- Main controllers: `StoryController`, `StorySessionController`, `ChapterController`, `AIGenerationController`

**Service Layer** (`service/`)
- Business logic and orchestration with `@Service`
- Transactional operations with `@Transactional`
- Key services: `StoryService`, `StorySessionService`, `ChapterService`, `AIStoryGenerator` implementations
- AI provider abstraction: `AIStoryGenerator` interface with `OpenAIStoryGenerator` and `ClaudeStoryGenerator` implementations
- Caching service: `AICacheService` for AI response caching

**Repository Layer** (`repository/`)
- JPA data access with Spring Data JPA `@Repository`
- Custom query methods following Spring Data naming conventions
- Repositories: `StoryRepository`, `StorySessionRepository`, `StoryChapterRepository`, `UserRepository`, `AICacheRepository`

**Entity Layer** (`entity/`)
- JPA entities with `@Entity`
- Core domain models: `Story`, `StorySession`, `StoryChapter`, `User`, `UserChoice`, `AICache`
- Uses Hibernate annotations for timestamps (`@CreationTimestamp`, `@UpdateTimestamp`)

**DTO Layer** (`DTO/`)
- Request/response DTOs for API contracts
- Separates internal entities from external API representation
- Examples: `CreateStoryRequest`, `StoryResponse`, `GenerateStoryRequest`, `GeneratedStoryResponse`

### AI Provider System

The application uses a **Strategy Pattern** for AI providers:
- `AIStoryGenerator` interface defines the contract
- `ClaudeStoryGenerator` and `OpenAIStoryGenerator` implement different AI providers
- `AIConfiguration` dynamically selects provider based on `ai.provider` property
- `AICacheService` caches AI responses to reduce API costs (7-day default TTL)
- AI requests include story generation and choice generation for interactive storytelling

### Security Architecture

- Spring Security configured in `SecurityConfig`
- JWT token support configured (not fully implemented yet)
- Currently permits all requests - **authentication is planned but not enforced**
- CSRF disabled for API endpoints
- JWT utilities in `JwtUtil` for future token-based authentication

### Database Schema

PostgreSQL database with Flyway migrations (`db/migration/`):
- `users` - User accounts with authentication
- `stories` - Story metadata (title, genre, difficulty, creator)
- `story_chapters` - Story content organized in chapters
- `story_sessions` - User gameplay sessions with progress tracking
- `user_choices` - Records of choices made during gameplay
- `ai_cache` - Cached AI responses for performance
- `ai_generations` - AI generation request logging

## Development Conventions

### Code Style
- **Google Java Format** enforced via Spotless Maven plugin
- 2-space indentation for Java, YAML, XML, and properties files
- Import order: `java|javax, jakarta, org, com, com.storyai`
- Always run `.\mvnw spotless:apply` before committing

### Naming Conventions
- Entities: Singular nouns (e.g., `Story`, `User`)
- Services: Domain name + "Service" (e.g., `StoryService`)
- Repositories: Domain name + "Repository" (e.g., `StoryRepository`)
- Controllers: Domain name + "Controller" (e.g., `StoryController`)
- DTOs: Purpose + Domain + "Request"/"Response" (e.g., `CreateStoryRequest`, `StoryResponse`)
- Methods: camelCase following Java conventions (e.g., `publishStory`, `updateSession`)

### Package Structure
```
com.storyai.storytelling_backend/
├── config/          # Spring configuration classes
├── controller/      # REST controllers
├── DTO/             # Data Transfer Objects
├── entity/          # JPA entities
├── exception/       # Custom exceptions and handlers
├── repository/      # Spring Data repositories
└── service/         # Business logic services
```

### Error Handling
- `GlobalExceptionHandler` with `@ControllerAdvice` for centralized exception handling
- Custom exceptions: `NotFoundException` for 404 scenarios
- Standard HTTP status codes for responses

### Database Migrations
- Flyway manages schema versions in `src/main/resources/db/migration/`
- Naming: `V{version}__{description}.sql` (e.g., `V1__Create_users_table.sql`)
- Migrations run automatically on startup
- Never modify existing migrations - create new ones for schema changes

## Configuration

### Environment Variables Required
```
# Database
DB_URL=jdbc:postgresql://localhost:5432/storytelling_db
DB_USERNAME=storyteller
DB_PASSWORD=storyai

# Server
SERVER_PORT=8080

# JWT (for future authentication)
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# AI Provider Selection
AI_PROVIDER=openai  # or 'claude'

# OpenAI Configuration
OPENAI_API_KEY=your-openai-key
OPENAI_MODEL=gpt-4
OPENAI_MAX_TOKENS=1000
OPENAI_TEMPERATURE=0.7

# Claude Configuration  
CLAUDE_API_KEY=your-claude-key
CLAUDE_MODEL=claude-sonnet-4-5-20250929
CLAUDE_MAX_TOKENS=2000
CLAUDE_TEMPERATURE=0.7

# AI Caching
AI_CACHE_ENABLED=true
AI_CACHE_EXPIRATION_HOURS=168
```

Store these in `.env` file (gitignored) or set as system environment variables.

### API Documentation
- Swagger UI available at: `http://localhost:8080/swagger-ui.html`
- OpenAPI spec at: `http://localhost:8080/api-docs`
- Health check endpoint: `http://localhost:8080/actuator/health`

### API Versioning
- All endpoints prefixed with `/api/v1/`
- Current endpoints: `/api/v1/stories`, `/api/v1/sessions`, `/api/v1/chapters`, `/api/v1/ai/generate`

## Key Domain Concepts

### Story Creation Flow
1. Create a `Story` via `POST /api/v1/stories` (title, genre, description, difficulty)
2. Add `StoryChapter` objects via `POST /api/v1/stories/{id}/chapters`
3. Optionally publish story by setting `isPublic=true` via `PUT /api/v1/stories/{id}/publish`

### Interactive Session Flow
1. Start a `StorySession` via `POST /api/v1/sessions` with a storyId and userId
2. Retrieve current chapter via `GET /api/v1/sessions/{id}/current`
3. Generate choices via AI: `POST /api/v1/ai/generate-choices`
4. Make choice and advance: `POST /api/v1/sessions/{id}/choice`
5. Session tracks `currentChapter`, `isCompleted`, and timestamps

### AI Generation
- Stories can be AI-generated via `POST /api/v1/ai/generate-story`
- Choices can be AI-generated based on story context via `POST /api/v1/ai/generate-choices`
- Results are cached in `ai_cache` table to reduce API costs
- Cache cleanup runs daily at 3 AM via `CacheCleanupScheduler`

## Testing Strategy

Tests should be located in `src/test/java` with the same package structure.

### Test Types
- **Unit Tests**: Service layer with mocked repositories
- **Integration Tests**: Repository layer with `@DataJpaTest` and test containers
- **Controller Tests**: `@WebMvcTest` for endpoint validation
- **Security Tests**: Verify authentication/authorization (when implemented)

## Important Notes

- **Java 24** is required (specified in pom.xml)
- **Spring Boot 3.4.3** with Jakarta EE (not javax)
- Security is currently **permitAll** - JWT authentication is configured but not enforced
- AI provider selection is environment-driven via `AI_PROVIDER` variable
- Flyway `validate-on-migrate` is set to `false` - review migrations carefully
- Application shows SQL queries in console (useful for debugging, disable in production)
- Spotless enforces Google Java Format - violations will fail the build during `verify` phase
