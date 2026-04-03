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

## Authentication System

The backend now includes a complete JWT-based authentication system:

### **Features**
- **JWT Authentication**: Stateless token-based authentication
- **User Registration**: Email verification required (SendGrid integration)
- **Login/Logout**: Secure session management with refresh tokens
- **Password Security**: BCrypt encryption with validation
- **Protected Endpoints**: All story operations require authentication
- **Public Access**: Story listing and viewing are public

### **Authentication Flow**
1. **Register**: Create account with email verification
2. **Login**: Get JWT access and refresh tokens
3. **Protected Requests**: Include `Authorization: Bearer <token>` header
4. **Token Refresh**: Use refresh token to get new access token

### **Security Configuration**
- **JWT Secret**: Configured via `app.jwt.secret` environment variable
- **Token Expiration**: Configured via `app.jwt.expiration` (default: 24 hours)
- **Password Requirements**: Min 8 chars, uppercase, lowercase, digit, special character
- **Email Verification**: Required for account activation

### **API Access**
```bash
# Register new user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"TestPass123!","confirmPassword":"TestPass123!"}'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"testuser","password":"TestPass123!"}'

# Create story (requires authentication)
curl -X POST http://localhost:8080/api/v1/stories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{"title":"My Story","description":"A test story","genre":"fantasy"}'
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

## Current Implementation Status

### ✅ **Completed Features**
- **JWT Authentication**: Complete token-based authentication system
- **User Management**: Registration, login, logout with email verification
- **Story CRUD**: Create, read, update, delete operations
- **API Documentation**: Comprehensive OpenAPI/Swagger documentation
- **Database**: PostgreSQL with Flyway migrations
- **Security**: BCrypt password encryption, JWT validation
- **Error Handling**: Global exception handler with proper responses

### 🔧 **Authentication System**
The authentication system is fully implemented and working:

- **JWT Tokens**: Stateless authentication with configurable expiration
- **User Registration**: Email verification via SendGrid (requires API keys)
- **Protected Endpoints**: Story creation and user management require authentication
- **Public Access**: Story listing and viewing are publicly accessible
- **Password Security**: Strong validation with BCrypt encryption

### 🚧 **Known Limitations**
- **Email Service**: Requires SendGrid API keys for user registration
- **AI Generation**: Needs OpenAI/Claude API keys configured
- **Rate Limiting**: Configured but not implemented yet

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

---

## Authentication System Implementation Status ✅

The storytelling backend now includes a **complete JWT-based authentication system**:

### **Implemented Features**
- **JWT Authentication**: Stateless token-based authentication with configurable expiration
- **User Registration**: Email verification required (SendGrid integration)
- **Login/Logout**: Secure session management with refresh tokens
- **Password Security**: BCrypt encryption with strong validation
- **Protected Endpoints**: All story operations require authentication
- **Public Access**: Story listing and viewing are publicly accessible
- **Security Configuration**: Proper Spring Security setup with JWT filter

### **Authentication Flow**
1. **Register**: Create account → Email verification → Account activation
2. **Login**: Get JWT access and refresh tokens
3. **Protected Requests**: Include `Authorization: Bearer <token>` header
4. **Token Refresh**: Use refresh token to get new access token

### **Security Configuration**
- **JWT Secret**: Configured via `JWT_SECRET` environment variable
- **Token Expiration**: Configured via `JWT_EXPIRATION_MS` (default: 24 hours)
- **Password Requirements**: Min 8 chars, uppercase, lowercase, digit, special character
- **Email Verification**: Required for account activation

### **API Usage Examples**
```bash
# Register new user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"TestPass123!","confirmPassword":"TestPass123!"}'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"testuser","password":"TestPass123!"}'

# Create story (requires authentication)
curl -X POST http://localhost:8080/api/v1/stories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{"title":"My Story","description":"A test story","genre":"fantasy"}'
```

### **Current Status**
- ✅ **Authentication System**: Fully implemented and working
- ✅ **JWT Token Management**: Generation, validation, and refresh working
- ✅ **User Association**: Stories now created with actual user (not null)
- ⚠️ **Email Service**: Requires SendGrid API keys for registration
- ✅ **API Security**: Proper public/protected endpoint separation

Database credentials in docker-compose match the `.env.example` defaults.
