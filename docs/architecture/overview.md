# Architecture Overview

## Introduction

SmartGym IoT Platform is structured as a **modular monolith** following **Hexagonal Architecture** (also called Ports & Adapters) principles. The design ensures that core business logic is fully isolated from infrastructure details (database, HTTP, MQTT, external services), making the codebase highly testable and incrementally extractable into microservices.

---

## Why a Modular Monolith?

A microservices-first approach adds significant operational complexity before you have proven your domain model. A modular monolith gives you:

- **Fast local development** — single process, single database schema, simple debugging
- **Strong module boundaries** — enforced by package naming and dependency rules
- **Low operational overhead** — one deployment artifact, one CI pipeline at MVP
- **Incremental extraction path** — each module is a candidate microservice once it stabilises

When a module (e.g., `recognition`) needs independent scaling or a different runtime, it can be extracted with minimal refactoring because its boundaries are already clean.

---

## Hexagonal Architecture

Each module is structured around an inner **domain** ring, surrounded by **ports** (interfaces) and **adapters** (implementations):

```
                  ┌─────────────────────────────────┐
                  │           <<module>>            │
                  │                                 │
  HTTP/MQTT ───► │  ┌─────────────────────────┐   │ ◄─── Tests
                  │  │  Driving Adapters        │   │
                  │  │  (REST controllers,      │   │
                  │  │   MQTT listeners)        │   │
                  │  └────────────┬────────────┘   │
                  │               │                 │
                  │  ┌────────────▼────────────┐   │
                  │  │  Application Services   │   │
                  │  │  (Use cases / Commands) │   │
                  │  └────────────┬────────────┘   │
                  │               │                 │
                  │  ┌────────────▼────────────┐   │
                  │  │  Domain Model           │   │
                  │  │  (Entities, VOs,        │   │
                  │  │   Domain Events)        │   │
                  │  └────────────┬────────────┘   │
                  │               │                 │
                  │  ┌────────────▼────────────┐   │
                  │  │  Driven Adapters         │   │
                  │  │  (JPA repos, Redis,      │   │
                  │  │   HTTP clients)          │   │
                  │  └─────────────────────────┘   │
                  │                                 │
                  └─────────────────────────────────┘
```

### Ports (interfaces)

- **Input ports** — defined as Java interfaces in `application/port/in/`. Controllers call these.
- **Output ports** — defined as Java interfaces in `application/port/out/`. Services depend on these; adapters implement them.

### Adapters

- **Driving (primary) adapters** — REST controllers, MQTT message listeners, WebSocket handlers
- **Driven (secondary) adapters** — JPA persistence adapters, Redis adapters, HTTP clients (recognition service)

---

## Module Layout (per domain module)

```
com.smartgym.<module>/
├── domain/
│   ├── model/          # Entities and Value Objects
│   └── event/          # Domain events
├── application/
│   ├── port/
│   │   ├── in/         # Input port interfaces (use cases)
│   │   └── out/        # Output port interfaces (repositories, gateways)
│   └── service/        # Use case implementations
└── adapter/
    ├── in/
    │   └── web/        # REST controllers
    └── out/
        └── persistence/ # JPA entities, Spring Data repositories, mappers
```

---

## Cross-Cutting Concerns

All modules share utilities from `com.smartgym.shared`:

| Package | Contents |
|---|---|
| `shared.api` | `ApiResponse`, `ErrorResponse`, `PageResponse` |
| `shared.exception` | `GlobalExceptionHandler`, domain exception base classes |
| `shared.security` | JWT filter, `SecurityConfig` |
| `shared.config` | MQTT config, Redis config, OpenAPI config |

---

## Service Boundaries

| Service | Runtime | Responsibility |
|---|---|---|
| `smartgym-api` | JVM (Java 21) | All business logic, REST API, MQTT ingestion, persistence |
| `recognition-service` | Python 3.11 | Face embedding extraction and recognition |

Communication between services is **synchronous HTTP** at MVP (recognition-service exposes a REST endpoint). The API calls it as a driven adapter behind an output port, so the transport can be changed later.

---

## Future Microservice Extraction Path

When ready, each module can be extracted as follows:

1. Move domain + application layers to a new Maven module / repo
2. Replace the in-process adapter calls with HTTP/gRPC/event-based adapters
3. Add an API gateway (e.g., Spring Cloud Gateway) in front

Modules most likely to be extracted first: `recognition`, `analytics`, `device`.

---

## Technology Decisions

| Decision | Rationale |
|---|---|
| Java 21 | Virtual threads (Project Loom), records, sealed classes |
| Spring Boot 3 | Mature, extensive ecosystem, first-class Micrometer support |
| Hexagonal architecture | Testability, separation of concerns, extraction readiness |
| Flyway | Version-controlled, repeatable migrations |
| JJWT | Lightweight JWT library with modern API |
| FastAPI (Python) | High performance, OpenCV ecosystem, async-ready |
| Mosquitto | Lightweight, standards-compliant MQTT broker |
| Redis | Low-latency cache, pub/sub for future event fan-out |
