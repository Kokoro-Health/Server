# API Improvements for Kokoro Server

This document outlines areas where the API usage could be made cleaner and more user-friendly.

---

## 1. API Endpoint Structure and Naming Conventions

### Issue: Inconsistent URL Patterns
**Files:** Multiple controllers
- `/users/profiles` (ProfileController) - should be `/users/profile` (singular)
- `/energy/add` (EnergyController) - the `/add` suffix is redundant with POST verb
- `/journals/{id}` with POST verb (JournalController) - should use PUT for updates
- `/auth/passkeys/auth/*` (PasskeyController) - mixing auth context with passkeys resource

**Why improve:** RESTful conventions suggest resources should be plural at the collection level but endpoints shouldn't mix verbs in URLs when HTTP verbs convey the action.

**Suggestions:**
- Change `/users/profiles` → `/users/profile` (it's about the current user's single profile)
- Change `POST /energy/add` → `POST /energy` or `POST /energy/entries`
- Change `POST /journals/{id}` → `PUT /journals/{id}`
- Restructure passkey auth: `POST /auth/passkeys/auth/start` → `POST /auth/passkey-authentication/start`

### Issue: Inconsistent Date/Time Parameter Handling
**Files:** EnergyController.kt (line 26, 60-61)
```kotlin
@GetMapping("/{date}")
fun getEnergyEntriesForDay(@PathVariable date: Instant)

@GetMapping("/recent")
fun getEnergyForDateRange(@RequestParam("from") from: Instant, @RequestParam("to") to: Instant)
```

**Why improve:** Using `Instant` for date-based queries is semantically incorrect - Instant represents a point in time, not a date. This can cause timezone confusion.

**Suggestions:**
- Use `LocalDate` for date-based queries
- Add timezone context to the API (already in user settings, leverage it)
- Consider renaming `/recent` → `/range` or `/history` for clarity

### Issue: Missing Version Prefix
**Files:** All controllers

**Why improve:** API versioning is critical for backwards compatibility. Currently there's no version in the URL structure.

**Suggestions:**
- Add `/api/v1/` prefix to all endpoints
- Configure in a base path property rather than repeating in each controller

---

## 2. DTO/Request/Response Models

### Issue: Inconsistent DTO Naming
**Files:** SettingsDtos.kt (line 19)
```kotlin
@Suppress("unused")
class SettingsResponseDto(
```
Why is this a `class` instead of `data class`? The `@Suppress("unused")` suggests tooling issues.

**Why improve:** Inconsistency makes the codebase harder to maintain. All other DTOs are data classes.

**Suggestions:**
- Convert to `data class`
- If there's a specific reason it can't be a data class, document it

### Issue: Mixed Validation Approaches
**Files:** AuthDtos.kt, EnergyDtos.kt, MfaDtos.kt
- Some use `@field:Size` (AuthDtos line 18)
- Some use `@field:Min/@field:Max` (EnergyDtos line 22)
- Inconsistent `@Schema(nullable = true)` usage

**Why improve:** Inconsistent validation patterns make it harder to understand what's required vs optional.

**Suggestions:**
- Standardize on Kotlin nullable types (`?`) for optional fields
- Remove `@field:Schema(nullable = true)` - OpenAPI should infer from Kotlin nullability
- Create custom validation annotations for common patterns

### Issue: Primitive Obsession
**Files:** Multiple DTOs
```kotlin
data class EnergyRequestDto(
    @field:Min(0) @field:Max(100) val amount: Int,
    ...
)
```

**Why improve:** Using primitives for domain concepts loses type safety and validation context.

**Suggestions:**
- Create value objects: `EnergyLevel(value: Int)` with validation
- Create `EmailAddress`, `VerificationCode`, etc. value types
- Centralize validation logic in the value objects

### Issue: Response DTOs Expose Implementation Details
**Files:** ProfileResponseDto.kt (line 16)
```kotlin
val profilePictureUrl: String,
```
But in ProfileMapper.kt (line 15-16), it defaults to a DuckDuckGo URL if null.

**Why improve:** The DTO claims it's non-null but relies on mapper logic. Frontend gets a hardcoded URL.

**Suggestions:**
- Make `profilePictureUrl` nullable in DTO
- Let frontend handle placeholder logic
- Or create a proper default profile picture service

### Issue: Timestamp Inconsistency
**Files:** Multiple DTOs
- Some use `Instant` (JournalDtos.kt)
- Some use `Long` epoch millis (ProfileResponseDto.kt line 17)

**Why improve:** Inconsistent timestamp formats confuse API consumers.

**Suggestions:**
- Standardize on ISO-8601 strings via Jackson serialization
- Configure Jackson to serialize `Instant` as ISO-8601
- Remove manual `.toEpochMilli()` conversions

---

## 3. Error Handling and Validation

### Issue: Generic Exception Handling
**Files:** GlobalExceptionHandler.kt (line 30-33)
```kotlin
@ExceptionHandler(Exception::class)
fun handleException(e: Exception): ResponseEntity<ErrorResponseDto> {
    return ResponseEntity.badRequest().body(ErrorResponseDto(e.message ?: "Internal server error"))
}
```

**Why improve:**
- Returns 400 (Bad Request) for ALL exceptions, even 500-level errors
- Exposes internal exception messages to users
- No logging of stack traces
- Returns "Internal server error" with 400 status code (semantic mismatch)

**Suggestions:**
- Create custom exception hierarchy (ValidationException, ResourceNotFoundException, UnauthorizedException, etc.)
- Return appropriate status codes (400, 401, 403, 404, 409, 500)
- Add request ID correlation for debugging
- Log exceptions with stack traces
- Never expose internal exception messages in production

### Issue: Using IllegalArgumentException for Business Logic
**Files:** SignUp.kt (line 23-24), SignIn.kt (line 19, 30), ResetPassword.kt, etc.
```kotlin
if (!command.tosAccepted) throw IllegalArgumentException("You must accept the Terms of Service.")
if (userRepository.existsByEmail(command.email)) throw IllegalArgumentException("This email is already in use.")
```

**Why improve:** `IllegalArgumentException` is for programming errors, not business rule violations. It also gets caught by the generic handler and becomes 400.

**Suggestions:**
- Create domain-specific exceptions: `TosNotAcceptedException`, `EmailAlreadyExistsException`
- Create a base `BusinessException` class
- Map to appropriate HTTP status codes in global handler
- Include error codes for i18n on frontend

### Issue: Inconsistent Error Response Structure
**Files:** ErrorResponseDto.kt
```kotlin
data class ErrorResponseDto(val message: String)
```

**Why improve:**
- No error codes for programmatic handling
- No field-level validation errors
- No support for multiple errors
- No timestamp or request correlation ID

**Suggestions:**
```kotlin
data class ErrorResponseDto(
    val timestamp: Instant,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val requestId: String?,
    val errors: List<FieldError> = emptyList()
)

data class FieldError(
    val field: String,
    val message: String,
    val rejectedValue: Any?
)
```

### Issue: No Validation on Path/Query Parameters
**Files:** AuthController.kt (line 61, 76)
```kotlin
@PostMapping("/reset-password")
fun requestPasswordReset(@RequestParam @Email @Valid email: String)

@GetMapping("/validate-code")
fun validatePasswordResetCode(@RequestParam code: String)
```

**Why improve:** `code` parameter has no validation. Could be empty, too long, etc.

**Suggestions:**
- Create wrapper DTOs for query params with validation
- Validate code format (should be 6 digits)

### Issue: ResponseStatusException in Use Cases
**Files:** AddEnergyEntry.kt (line 16, 20)
```kotlin
throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS)
```

**Why improve:** Use cases (application layer) shouldn't depend on Spring Web (HTTP concepts). Violates clean architecture.

**Suggestions:**
- Create domain exceptions: `UnauthorizedException`, `RateLimitExceededException`
- Map these in controller or global exception handler
- Keep application layer framework-agnostic

---

## 4. Code Organization and Layering

### Issue: Inconsistent User Extraction Pattern
**Files:** Multiple controllers
```kotlin
// Pattern 1: SecurityContextHolder
val user = SecurityContextHolder.getContext().authentication.principal as User

// Pattern 2: @AuthenticationPrincipal
@AuthenticationPrincipal user: User
```

**Why improve:** Two different patterns for the same thing creates confusion.

**Suggestions:**
- Standardize on `@AuthenticationPrincipal user: User` (cleaner, testable)
- Remove all `SecurityContextHolder.getContext()` calls from controllers
- Consider creating a `@CurrentUser` custom annotation

### Issue: Mappers Are Inconsistent
**Files:** Various mapper classes
- Some are named `*Mapper` (ProfileMapper, AuthDtoMapper)
- Some are `*DtoMapper` (SettingsDtoMapper, PasskeyDtoMapper)
- Some map both ways, some only one way
- No clear pattern for when to use mapper vs inline mapping

**Why improve:** Inconsistency makes code harder to navigate.

**Suggestions:**
- Standardize on `*DtoMapper` naming
- Create base interface: `DtoMapper<Domain, RequestDto, ResponseDto>`
- Use MapStruct for complex mappings to reduce boilerplate

### Issue: Business Logic in Controllers
**Files:** EnergyController.kt (line 32-42)
```kotlin
EnergyDetailsResponseDto(
    entries = details.entries.map {
        EnergyInfoDateResponseDto(...)
    },
    influentialNegative = details.influentialNegative.let { ReasonAmountResponseDto(it.reason, it.level) },
    ...
)
```

**Why improve:** Mapping logic should be in mapper classes, not controllers.

**Suggestions:**
- Create EnergyDtoMapper
- Move all mapping logic there
- Controllers should only orchestrate: validate → call use case → map → return

### Issue: Use Case Command/Response Pattern Not Consistently Applied
**Files:** Various use cases
- Some use cases have inner `Command`/`Response` classes (SignIn, SignUp)
- Others just take primitive parameters (AddEnergyEntry, UpdateCurrentJournal)
- Inconsistent naming (UpdateCurrentJournal has Response, GetProfile has Response)

**Why improve:** Inconsistency makes the codebase harder to understand and maintain.

**Suggestions:**
- Standardize: all use cases should have Command (if params > 1) and Response
- Use sealed classes for variants (e.g., SignInResponse.MfaRequired vs Success)
- Consider CQRS pattern separation

### Issue: Repository Pattern Leaking to Controllers
**Files:** JournalController.kt (line 37)
```kotlin
val contentSaved = updateCurrentJournal.execute(user, id, content.content)
```
Then another method (line 48) calls the same use case with `null` id.

**Why improve:** Two controller methods for the same operation suggests the use case API is awkward.

**Suggestions:**
- Consolidate to single endpoint: `POST /journals` (create) and `PUT /journals/{id}` (update)
- Or make id optional in single method: `POST /journals/{id?}`

---

## 5. Consistency Across Endpoints

### Issue: Return Types Vary
**Files:** All controllers
- Some return `ResponseEntity<Unit>` (line 43 ProfileController)
- Some return `ResponseEntity<Any>` (line 51 AuthController)
- Some return `ResponseEntity<Dto>` (line 30 ProfileController)
- Inconsistent use of `.build()` vs `.body(...)`

**Why improve:** Inconsistency and `Any`/`Unit` types provide no API contract.

**Suggestions:**
- Always return specific DTO types, even for "empty" responses
- Create standard response wrappers: `ApiResponse<T>`
- Never use `Any` - use `Unit` or empty response DTO
- For 204 No Content, use `ResponseEntity<Void>` (Java's Void, not Kotlin Unit)

### Issue: HTTP Status Code Inconsistency
**Files:** Multiple controllers
- `ProfileController.updateProfile` returns 200 OK (line 44)
- `SettingsController.updateSettings` returns 204 No Content (line 33)
- Both are PUT/POST updates with no body

**Why improve:** Inconsistent status codes for similar operations confuse API consumers.

**Suggestions:**
- Standardize: POST (create) → 201 Created with location header
- PUT (update) → 200 OK with body or 204 No Content
- DELETE → 204 No Content
- Document in API standards document

### Issue: No Pagination
**Files:** JournalController.kt (line 70), EnergyController.kt (line 59)
```kotlin
@GetMapping("/recent")
fun getRecentJournalsShort(): ResponseEntity<List<ShortJournalResponseDto>>
```

**Why improve:** Returning unbounded lists will cause performance issues as data grows.

**Suggestions:**
- Implement pagination with `Pageable` parameter
- Return `Page<T>` wrapper with metadata (page, size, totalPages, totalElements)
- Add default limits even without pagination

### Issue: No HATEOAS Links
**Files:** All response DTOs

**Why improve:** REST Level 3 APIs include hypermedia links for discoverability.

**Suggestions:**
- Add `_links` to response DTOs
- Use Spring HATEOAS for link generation
- Include self, related resources links

---

## 6. Documentation and OpenAPI/Swagger Setup

### Issue: Missing OpenAPI Annotations
**Files:** All controllers and DTOs
- No `@Operation` descriptions
- No `@Parameter` descriptions
- No `@ApiResponse` annotations for specific endpoints
- No example values

**Why improve:** Generated OpenAPI docs are minimal and unhelpful to API consumers.

**Suggestions:**
```kotlin
@Operation(
    summary = "Get user profile",
    description = "Returns the authenticated user's profile information"
)
@ApiResponses(
    ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
    ApiResponse(responseCode = "401", description = "Not authenticated")
)
@GetMapping
fun getMyProfile(): ResponseEntity<ProfileResponseDto>
```

### Issue: Strict DTO Naming Validator May Be Too Restrictive
**Files:** OpenApiConfig.kt (line 79-99)
```kotlin
fun dtoNamingConventionValidator(): OpenApiCustomizer {
    ...
    if (!lcName.contains("request") && !lcName.contains("response")) {
        throw IllegalStateException(...)
    }
}
```

**Why improve:** This fails at startup for any DTO that doesn't contain "Request" or "Response", which may be too strict for wrapper types, shared types, etc.

**Suggestions:**
- Allow exceptions for specific types (like `ErrorResponseDto`, `PageDto`)
- Consider warning instead of error
- Document naming conventions in README

### Issue: No API Versioning in OpenAPI
**Files:** OpenApiConfig.kt

**Why improve:** OpenAPI spec should include version information.

**Suggestions:**
```kotlin
@Bean
fun customOpenAPI(): OpenAPI {
    return OpenAPI()
        .info(Info()
            .title("Kokoro API")
            .version("1.0.0")
            .description("Health tracking API")
            .contact(Contact().email("support@kokoro.health"))
        )
        .servers(listOf(
            Server().url("https://api.kokoro.health").description("Production"),
            Server().url("http://localhost:8080").description("Development")
        ))
        ...
}
```

---

## 7. Authentication/Authorization Patterns

### Issue: Cookie vs Bearer Token Confusion
**Files:** AuthController.kt, JwtFilter.kt
- SignIn/SignUp set cookies (line 25-28 AuthController)
- JwtFilter reads from Authorization header (line 17 JwtFilter)
- No clear documentation which method to use

**Why improve:** Confusing for API consumers. Should support one or both explicitly.

**Suggestions:**
- Support both cookie and Bearer token
- Document clearly when to use which (cookie for browser, bearer for mobile/API)
- JwtFilter should check both sources

### Issue: No JWT Token Refresh Mechanism
**Files:** JWT-related files

**Why improve:** 7-day expiration with no refresh means users must re-authenticate weekly.

**Suggestions:**
- Implement refresh token pattern
- Add `POST /auth/refresh` endpoint
- Use short-lived access tokens (15min) + long-lived refresh tokens (30 days)

### Issue: Hardcoded Security Values
**Files:** SecurityBeans.kt (line 92-97)
```kotlin
RelyingPartyIdentity.builder()
    .id("kokoro.health")
    .name("Kokoro")
    .build()
```
And line 97:
```kotlin
.origins(setOf("https://kokoro.health"))
```

**Why improve:** Hardcoded values don't work in dev/staging environments.

**Suggestions:**
- Move to configuration properties
- Use profile-specific values
- Support multiple origins from config

### Issue: No Rate Limiting Configuration
**Files:** AddEnergyEntry.kt handles it manually

**Why improve:** Rate limiting is cross-cutting and should be centralized.

**Suggestions:**
- Use Spring's RateLimiter or Bucket4j
- Apply via interceptor or annotation
- Configure limits externally

### Issue: Password Validation Rules Not Exposed to Frontend
**Files:** PasswordValidator.kt

**Why improve:** Frontend needs same validation rules to provide instant feedback.

**Suggestions:**
- Create `GET /auth/password-requirements` endpoint
- Return rules as JSON
- Keep validation logic in one place

---

## 8. Common Patterns That Could Be Improved

### Issue: Repetitive User ID Extraction
**Files:** Multiple use cases
```kotlin
val userId = user.id ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
```

**Why improve:** Nullable ID for authenticated user suggests architectural issue.

**Suggestions:**
- User should always have ID after authentication
- Make User.id non-nullable for authenticated users
- Or create `AuthenticatedUser` type with non-null ID

### Issue: No Soft Delete Pattern
**Files:** Repository interfaces

**Why improve:** Deleting user data permanently violates GDPR "right to be forgotten" logging requirements.

**Suggestions:**
- Implement soft delete with `deletedAt` timestamp
- Add `@Where(clause = "deleted_at IS NULL")` to entities
- Keep audit trail for compliance

### Issue: No Audit Logging
**Files:** All use cases

**Why improve:** No audit trail for security-sensitive operations.

**Suggestions:**
- Add AOP aspect for auditing: `@Audited` annotation
- Log who did what when (user ID, action, timestamp, IP)
- Store in separate audit table

### Issue: Missing Transactional Boundaries
**Files:** Use cases
- No consistent `@Transactional` annotations
- Unclear which operations are atomic

**Why improve:** Data consistency issues may arise.

**Suggestions:**
- Add `@Transactional` to all use cases that modify data
- Configure read-only transactions for queries
- Consider Saga pattern for distributed transactions

### Issue: No Caching Strategy
**Files:** All read operations

**Why improve:** Repeated database queries for relatively static data (user profile, settings).

**Suggestions:**
- Add Spring Cache abstraction
- Cache user profile with short TTL
- Cache settings until update
- Use Redis for distributed caching

### Issue: Timezone Handling
**Files:** Multiple files using `Instant.now()`

**Why improve:** All timestamps are UTC but user settings include timezone (ProfileResponseDto line 19). No clear pattern for conversion.

**Suggestions:**
- Store all times as UTC (good, you're doing this)
- Convert to user timezone in API response using user's settings
- Add ZonedDateTime support in DTOs for clarity

---

## 9. Missing Abstractions or Repetitive Code

### Issue: Duplicate Code Generation Logic
**Files:** ResetPassword.kt (line 71-74), RequestVerificationCode.kt (line 50-55)

Both generate 6-digit verification codes differently.

**Why improve:** Code duplication, inconsistent approaches.

**Suggestions:**
- Create `VerificationCodeGenerator` service
- Centralize code generation and validation
- Make format configurable

### Issue: No Base Controller
**Files:** All controllers

**Why improve:** Repeated patterns in all controllers (validation setup, user extraction).

**Suggestions:**
```kotlin
@Validated
abstract class BaseController {
    protected fun getCurrentUser(): User {
        return SecurityContextHolder.getContext().authentication.principal as User
    }
}
```

### Issue: Email Sending Repetition
**Files:** ResetPassword.kt (line 26-36), RequestVerificationCode.kt (line 39-46)

Similar patterns for sending templated emails.

**Why improve:** Could be abstracted.

**Suggestions:**
- Create EmailTemplate enum
- Create `EmailService` with type-safe template methods
- Example: `emailService.sendPasswordReset(user, code)`

### Issue: No Shared Constants
**Files:** Multiple files
- `EXPIRATION_MINUTES = 15L` repeated
- `CODE_LENGTH = 6` repeated
- Magic numbers throughout

**Why improve:** Changes require updating multiple files.

**Suggestions:**
- Create `ApiConstants` object
- Create domain-specific constant objects: `VerificationConstants`, `SecurityConstants`

---

## 10. Configuration and Environment Setup

### Issue: Missing Environment Variables in example.env
**Files:** example.env vs compose.yaml and application.yml
- `FRONTEND_URL` used in compose.yaml (line 27) but not in example.env
- `ENCRYPTION_PROVIDER` used in application.yml (line 44) but not in example.env
- `VAULT_ADDR`, `VAULT_TOKEN`, `VAULT_PATH` used but not in example.env

**Why improve:** Incomplete example causes configuration errors for new developers.

**Suggestions:**
- Add all required variables to example.env
- Add comments explaining each variable
- Consider using `.env.template` for clarity

### Issue: Inconsistent Configuration Property Naming
**Files:** Multiple config files
- Some use `kokoro.*` prefix
- Some use `spring.*` prefix
- Inconsistent kebab-case vs camelCase

**Why improve:** Configuration is hard to discover and use.

**Suggestions:**
- Standardize on `kokoro.*` for app-specific properties
- Use kebab-case consistently in YAML
- Document all properties in README

### Issue: No Configuration Validation
**Files:** Config classes
```kotlin
class JwtConfig {
    lateinit var secret: String
    ...
}
```

**Why improve:** App starts even with missing/invalid config, fails at runtime.

**Suggestions:**
- Add `@Validated` and `@NotBlank` annotations
- Implement `@PostConstruct` validation methods
- Fail fast at startup with clear error messages

### Issue: Development vs Production Config Gaps
**Files:** application-dev.yml vs application-prod.yml
- Dev has swagger enabled, prod disabled (good)
- But dev uses static encryption key from env var (security risk if env leaked)

**Why improve:** Dev environment should mirror prod more closely for security testing.

**Suggestions:**
- Use Vault even in dev with Docker compose
- Document security differences clearly
- Add security checklist for prod deployment

### Issue: Hardcoded Year in Email Templates
**Files:** ResetPassword.kt (line 33), RequestVerificationCode.kt (line 43)
```kotlin
"year" to 2026
```

**Why improve:** Will need annual updates.

**Suggestions:**
- Use `Year.now().value`
- Or handle in email template library

### Issue: No Health Check Details
**Files:** application.yml (line 30-35)

Only exposes basic health endpoint.

**Why improve:** Limited observability in production.

**Suggestions:**
- Add custom health indicators (database, mail server, vault)
- Add metrics endpoint with Micrometer
- Expose readiness/liveness probes for Kubernetes

### Issue: No API Documentation for Developers
**Why improve:** No README for API, no getting started guide, no architecture documentation.

**Suggestions:**
- Add API README with:
  - Getting started
  - Architecture overview (layers explanation)
  - How to add new endpoint
  - Testing guidelines
  - Deployment guide
- Add inline documentation for complex business logic
- Create ADR (Architecture Decision Records) for key decisions

---

## Summary Priority Ranking

### Critical (Fix Soon)
1. Generic exception handler returning 400 for all errors
2. ResponseStatusException in application layer
3. Inconsistent user extraction pattern
4. Missing configuration validation
5. No pagination on list endpoints
6. Cookie vs Bearer token confusion

### High (Next Sprint)
7. Inconsistent DTO patterns and validation
8. No custom exception hierarchy
9. Timestamp format inconsistency
10. Missing OpenAPI annotations
11. Repetitive code generation logic
12. Incomplete example.env

### Medium (Technical Debt)
13. No API versioning
14. RESTful URL pattern issues
15. No JWT refresh mechanism
16. Date vs Instant confusion
17. Missing base controller
18. No caching strategy

### Low (Nice to Have)
19. HATEOAS links
20. Soft delete pattern
21. Audit logging
22. MapStruct integration
