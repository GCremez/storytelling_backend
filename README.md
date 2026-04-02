# Storytelling Backend

A Spring Boot backend for interactive fiction that generates stories using either OpenAI's GPT models or Anthropic's Claude. The project focuses on creating branching narratives with meaningful player choices.

## What This Actually Does

The core functionality is split between two AI story generators:

- **OpenAIStoryGenerator**: Uses GPT-4/GPT-3.5-turbo with a chat completions approach
- **ClaudeStoryGenerator**: Uses Claude Sonnet/Opus with Anthropic's messages API

Both implement the same `AIStoryGenerator` interface and include:
- Story chapter generation based on genre, theme, and tone
- Choice generation with consequences and emotional tones (Claude-specific feature)
- Response caching via `AICacheService` to reduce API costs
- Configurable temperature, max tokens, and model selection

## Architecture

```
StoryController → StoryService → {OpenAI|Claude}StoryGenerator → AICacheService
                ↓
            StoryRepository → PostgreSQL (with Flyway migrations)
```

Key design decisions:
- **Provider switching**: Controlled by `ai.provider` property, defaults to OpenAI
- **Caching strategy**: Cache keys include story ID, session ID, and content hash
- **Choice parsing**: Both providers parse structured responses, Claude includes emotional tones
- **Error handling**: Graceful fallbacks when API calls fail

## Getting Started

### Local Development

1. **Setup database**
   ```bash
   # PostgreSQL setup
   createdb storytelling_db
   # Or use the included docker-compose.yml
   docker-compose up postgres -d
   ```

2. **Configure environment**
   ```bash
   cp .env.example .env
   # Add your API keys to .env
   ```

3. **Run the app**
   ```bash
   ./mvnw spring-boot:run
   # Available at http://localhost:8080
   ```

### Docker (Simpler)

```bash
docker-compose up -d
# App at http://localhost:8081, DB at localhost:5432
```

## Environment Variables

Copy `.env.example` to `.env`:

```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/storytelling_db
DB_USERNAME=storyteller
DB_PASSWORD=storyai

# AI Provider (pick one)
AI_PROVIDER=openai          # or "claude"
OPENAI_API_KEY=sk-...
CLAUDE_API_KEY=sk-ant-...

# JWT
JWT_SECRET=your_secret_key_here
JWT_EXPIRATION_MS=86400000

# Optional: Email via SendGrid
EMAIL_FROM=noreply@yourdomain.com
SENDGRID_API_KEY=SG....
```

## API Endpoints

The actual endpoints you'll use:

```bash
# Get stories (with optional genre filter)
GET /api/v1/stories?genre=fantasy

# Get specific story
GET /api/v1/stories/{id}

# Create new story (currently no auth - see TODO in controller)
POST /api/v1/stories
{
  "title": "My Story",
  "description": "A tale of adventure",
  "genre": "fantasy"
}
```

**Note**: Authentication is partially implemented. The `StoryController` has a TODO comment about getting the current user from security context.

## Code Structure

```
src/main/java/com/storyai/storytelling_backend/
├── DTO/                    # Request/response objects
├── config/
│   └── AIProviderConfig.java  # AI provider configuration
├── controller/
│   └── StoryController.java   # Main REST endpoints
├── entity/
│   └── Story.java            # JPA entity
├── service/
│   ├── StoryService.java     # Business logic
│   ├── OpenAIStoryGenerator.java
│   ├── ClaudeStoryGenerator.java
│   └── AICacheService.java   # Redis/caching layer
└── repository/
    └── StoryRepository.java  # Data access
```

## AI Provider Differences

### OpenAI Implementation
- Uses chat completions API
- Simpler choice format: `CHOICE:` and `CONSEQUENCE:`
- Default model: `gpt-4`
- Cache keys: `story:{storyId}:{sessionId}:{hash}`

### Claude Implementation  
- Uses messages API with `anthropic-version: 2023-06-01`
- Enhanced choice format includes emotional tones:
  ```
  CHOICE: [action]
  CONSEQUENCE: [outcome]
  TONE: [brave/cautious/clever/aggressive/diplomatic]
  ```
- Default model: `claude-sonnet-4-5-20250929`
- Cache keys: `claude:story:{storyId}:{sessionId}:{hash}`

Both providers fallback to generic choices if parsing fails.

## Development Notes

### Code Quality
The project enforces Google Java Format via Spotless:
```bash
./mvnw spotless:check   # Verify formatting
./mvnw spotless:apply   # Auto-format code
```

### Database Migrations
Uses Flyway for schema management. Migration files go in `src/main/resources/db/migration/`.

### Testing
```bash
./mvnw test
```

## Current Limitations

- Authentication is incomplete (see TODO in `StoryController`)
- Email service exists but integration status unclear
- No rate limiting implementation despite config flag
- Cache service implementation not visible in explored files

## Docker Configuration

The `docker-compose.yml` sets up:
- PostgreSQL 17 with persistent volume
- Spring Boot app on port 8081 (mapped to internal 8080)
- Network isolation between services

Database credentials in docker-compose match the `.env.example` defaults.
