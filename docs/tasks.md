# Improvement Tasks Checklist

Below is an ordered, actionable checklist to improve the codebase across architecture, code quality, reliability, security, performance, and developer experience. Check items off as they are completed.

1. [ ] Create and enforce consistent code style and formatting
   - [ ] Introduce Checkstyle or Spotless with Google Java Style (configure in pom.xml)
   - [ ] Apply consistent naming conventions (e.g., method names like publishStory, updateSession in camelCase)
   - [ ] Enable IDE code style settings & EditorConfig

2. [ ] Strengthen layered architecture boundaries
   - [ ] Ensure controllers are thin (no business logic); move logic into services where needed
   - [ ] Introduce DTOs for requests/responses for all endpoints (avoid exposing entities)
   - [ ] Add a mapping layer (MapStruct or manual mappers) for Entity <-> DTO

3. [ ] Validation and error handling improvements
   - [ ] Add javax/jakarta validation annotations to DTOs (e.g., @NotBlank for CreateStoryRequest fields)
   - [ ] Add @Valid on controller method parameters
   - [ ] Expand GlobalExceptionHandler to handle MethodArgumentNotValidException with field errors
   - [ ] Replace generic RuntimeExceptions with custom exceptions (NotFoundException, ForbiddenException, BadRequestException)
   - [ ] Map custom exceptions to appropriate HTTP status codes
   - [ ] Replace ex.printStackTrace() with structured logging

4. [ ] Security hardening
   - [ ] Replace permitAll() for /api/v1/** with role/authority-based access control
   - [ ] Implement authentication (e.g., stateless JWT) and obtain current user from SecurityContext in controllers/services
   - [ ] Hide sensitive fields in responses (never expose passwordHash)
   - [ ] Add password hashing (BCrypt) on user creation and updates (if applicable)

5. [ ] Consistency and correctness in service layer
   - [ ] Standardize method naming (publishStory, updateSession) to follow Java conventions
   - [ ] Make service methods transactional with readOnly where appropriate; review write operations
   - [ ] Add authorization checks in service methods using the authenticated user

6. [ ] Repository and query optimization
   - [ ] Review StoryRepository methods for proper indexes (e.g., is_public, genre, created_at)
   - [ ] Add missing indexes via Flyway migrations where beneficial
   - [ ] Ensure pagination for listing endpoints (getPublicStories, getStoriesByGenre) using Pageable

7. [ ] Database schema and migrations
   - [ ] Review Flyway migrations for idempotency and correctness (fix V3 vs V3.1 consistency)
   - [ ] Add not-null constraints and sensible defaults (e.g., is_public default false)
   - [ ] Add foreign key constraints and cascading rules where appropriate
   - [ ] Align entity field lengths and column definitions with schema

8. [ ] API design enhancements
   - [ ] Document all endpoints with OpenAPI/Swagger (springdoc-openapi)
   - [ ] Use Problem Details (RFC 9457) format for error responses
   - [ ] Return Location header on resource creation (createStory)
   - [ ] Support filtering, sorting, and pagination on list endpoints

9. [ ] Observability and logging
   - [ ] Introduce a logging framework configuration (Logback) with JSON logging option for prod
   - [ ] Add request/response logging (filter or AOP) with PII scrubbing
   - [ ] Add application metrics with Micrometer and expose /actuator metrics
   - [ ] Define standard MDC (traceId) and correlate with logs

10. [ ] Performance and scalability
    - [ ] Use DTO projections for list endpoints to avoid N+1 and heavy entities
    - [ ] Evaluate FetchType and add @EntityGraph where necessary to prefetch relations
    - [ ] Add caching (e.g., @Cacheable for getPublicStories/getStoriesByGenre)

11. [ ] Reliability and resilience
    - [ ] Add timeouts and retries for external calls (if any) using Resilience4j
    - [ ] Add database transaction boundaries and propagation where needed
    - [ ] Ensure idempotency for create endpoints (e.g., idempotency keys if applicable)

12. [ ] Comprehensive testing
    - [ ] Add unit tests for services with mocks for repositories
    - [ ] Add WebMvc slice tests for controllers (validation, error mappings)
    - [ ] Add repository integration tests using Testcontainers (PostgreSQL) and Flyway
    - [ ] Add security tests verifying unauthorized/forbidden scenarios
    - [ ] Add test data builders and fixtures

13. [ ] CI/CD and build quality gates
    - [ ] Add GitHub Actions (or other CI) to run build, tests, lint, and checkstyle on PRs
    - [ ] Enforce coverage threshold with JaCoCo (e.g., 80%)
    - [ ] Produce and publish Docker images via CI

14. [ ] Configuration management
    - [ ] Split application properties by profile (application-dev.properties, application-prod.properties)
    - [ ] Externalize secrets via environment variables; remove any secrets from repo
    - [ ] Introduce typed configuration with @ConfigurationProperties

15. [ ] API versioning and stability
    - [ ] Keep versioned paths (/api/v1) and plan for /v2; avoid breaking changes
    - [ ] Add deprecation headers or documentation for future changes

16. [ ] Domain model enhancements
    - [ ] Add invariants and business rules (e.g., publishing only by owner) using domain services or policies
    - [ ] Ensure DifficultyLevel default is consistent at DB level and entity
    - [ ] Validate StorySession transitions (start, update, complete) and prevent invalid states

17. [ ] Developer ergonomics
    - [ ] Add Makefile/PowerShell scripts for common tasks (build, test, run, format)
    - [ ] Document local setup, running, and debugging in README.md
    - [ ] Add sample .env.example and docker-compose overrides for local dev

18. [ ] Endpoint improvements already hinted in code
    - [ ] Implement authentication usage in createStory to set createdBy from SecurityContext
    - [ ] Return 404 with a structured error when story not found (getStory)
    - [ ] Convert list mapping to use a mapper and support pagination params

19. [ ] Data privacy and GDPR
    - [ ] Review logs and responses to ensure no sensitive user data is exposed
    - [ ] Add data retention and deletion endpoints/policies for users and sessions

20. [ ] Housekeeping
    - [ ] Remove unused code, comments, and TODOs
    - [ ] Fix typos and method naming inconsistencies (PublishStory -> publishStory, UpdateSession -> updateSession)
    - [ ] Add missing equals/hashCode/toString where necessary (value objects)
