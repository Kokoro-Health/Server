# Kokoro Server

A modular Spring Boot application built with Kotlin, following clean architecture principles to ensure maintainability and separation of concerns.

## Architecture Overview

The project uses a layered hexagonal architecture, dividing responsibilities across four distinct modules:

```
┌─────────────────────────────────────────────┐
│                    API                      │
│         (REST Controllers & DTOs)           │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│              Application                    │
│           (Use Cases/Services)              │
└──────────────────┬──────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
┌───────▼──────┐    ┌─────────▼──────────────┐
│    Domain    │    │    Infrastructure       │
│ (Core Logic) │◄───┤  (Adapters & Impl.)     │
└──────────────┘    └────────────────────────┘
```

### Module Responsibilities

**Domain** - The heart of the application
- Contains business entities and core domain models
- Defines port interfaces (repository contracts)
- No external dependencies, purely domain logic
- Framework-agnostic and highly testable

**Application** - Business orchestration
- Implements use cases and application services
- Coordinates domain objects to fulfill business requirements
- Acts as the bridge between API and Domain layers
- Handles transaction boundaries and workflow logic

**Infrastructure** - Technical implementation
- Provides concrete implementations of domain ports
- Contains JPA entities, database mappers, and adapters
- Manages persistence, external APIs, and technical concerns
- Implements the dependency inversion principle

**API** - External interface
- Exposes REST endpoints for client communication
- Handles request/response DTOs and validation
- Maps between API contracts and application commands
- Entry point for all external interactions

## Technology Stack

- **Language**: Kotlin 2.1.0
- **Framework**: Spring Boot 3.4.2
- **JVM**: Java 21
- **Build Tool**: Gradle with Kotlin DSL
- **Database**: PostgreSQL (containerized via Docker Compose)

## Getting Started

### Prerequisites
- JDK 21
- Docker and Docker Compose

### Setup
1. Copy `example.env` to `local.env` and configure your environment variables
2. Start the database: `docker-compose up -d`
3. Run the application: `./gradlew :api:bootRun`

## Project Structure

Each module follows standard Maven/Gradle conventions with `src/main/kotlin` containing source code under the package `health.kokoro.<module>`.
