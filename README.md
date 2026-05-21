# SmartGym IoT Platform

> An intelligent gym management platform powered by IoT devices, computer vision, and event-driven architecture.

[![Java](https://img.shields.io/badge/Java-21-blue)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)
[![Python](https://img.shields.io/badge/Python-3.11-yellow)](https://www.python.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE)

---

## Project Vision

SmartGym IoT Platform integrates embedded hardware (ESP32 / ESP32-CAM), cloud services, and real-time analytics to deliver an intelligent gym management experience. The platform automates member check-in via face recognition, tracks equipment usage, sends MQTT-driven alerts, and exposes a rich API for staff dashboards and mobile apps.

The codebase is designed from the ground up for **extensibility**, **testability**, and **portfolio readability**—following Hexagonal Architecture and Domain-Driven Design principles inside a modular monolith that can be incrementally extracted into microservices.

---

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     SmartGym IoT Platform                   │
│                                                             │
│  ┌───────────────┐    MQTT     ┌─────────────────────────┐  │
│  │  ESP32 / CAM  │ ──────────► │  Mosquitto Broker       │  │
│  └───────────────┘             └──────────┬──────────────┘  │
│                                           │ subscribe        │
│  ┌────────────────────────────────────────▼──────────────┐  │
│  │             smartgym-api  (Spring Boot 3 / Java 21)   │  │
│  │  ┌──────────┐ ┌─────────┐ ┌──────────┐ ┌──────────┐  │  │
│  │  │ identity │ │ member  │ │ device   │ │attendance│  │  │
│  │  └──────────┘ └─────────┘ └──────────┘ └──────────┘  │  │
│  │  ┌──────────┐ ┌─────────┐                             │  │
│  │  │recognition│ │analytics│                             │  │
│  │  └──────────┘ └─────────┘                             │  │
│  └───────────────────────────────────────────────────────┘  │
│           │ HTTP/REST          │ PostgreSQL / Redis           │
│  ┌────────▼────────┐   ┌──────▼──────┐                      │
│  │ recognition-    │   │  PostgreSQL  │                      │
│  │ service (Python │   │  Redis       │                      │
│  │ / FastAPI)      │   └─────────────┘                      │
│  └─────────────────┘                                        │
│           │ Prometheus metrics                               │
│  ┌────────▼────────┐                                        │
│  │ Prometheus +    │                                        │
│  │ Grafana         │                                        │
│  └─────────────────┘                                        │
└─────────────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Core API | Java 21, Spring Boot 3, Spring Security, Spring Data JPA |
| Messaging | MQTT (Eclipse Paho), Mosquitto broker |
| Recognition service | Python 3.11, FastAPI, OpenCV, face_recognition |
| Database | PostgreSQL 16 |
| Cache / Pub-Sub | Redis 7 |
| Migrations | Flyway |
| Auth | JWT (JJWT) |
| Observability | Micrometer, Prometheus, Grafana |
| Containers | Docker, Docker Compose |
| Embedded | ESP32, ESP32-CAM (Arduino / PlatformIO) |
| CI | GitHub Actions |

---

## Repository Structure

```
Smart-IoT-Platform/
├── apps/
│   ├── smartgym-api/          # Spring Boot 3 core API
│   └── recognition-service/   # Python FastAPI face-recognition service
├── docs/
│   ├── architecture/
│   │   ├── overview.md
│   │   ├── domain-modules.md
│   │   └── face-recognition-design.md
│   └── mqtt/
│       └── topic-conventions.md
├── infra/
│   └── docker/
│       └── docker-compose.yml
├── .github/
│   └── workflows/
│       └── ci.yml
├── .editorconfig
├── .env.example
├── .gitignore
└── README.md
```

---

## MVP Scope

The Minimum Viable Product targets the following capabilities:

1. **Member registration** — CRUD API for gym members with profile photo upload
2. **Face-recognition check-in** — ESP32-CAM captures frame → recognition-service identifies member → attendance event recorded
3. **MQTT device integration** — ESP32 sensors publish equipment state; API subscribes and stores events
4. **JWT authentication** — Staff login, role-based access (ADMIN / STAFF / MEMBER)
5. **Attendance reporting** — Basic daily/weekly summaries via REST API
6. **Local infra** — Single `docker compose up` brings up all dependencies

---

## Roadmap

| Phase | Milestone | Status |
|---|---|---|
| 0 | Repository bootstrap, architecture docs, service skeletons | ✅ Done |
| 1 | JWT auth, member persistence, Flyway migrations | 🔄 Next |
| 2 | MQTT ingestion pipeline, device module | ⏳ Planned |
| 3 | Face recognition integration (Python ↔ Java) | ⏳ Planned |
| 4 | WebSocket real-time dashboard events | ⏳ Planned |
| 5 | Analytics module, Grafana dashboards | ⏳ Planned |
| 6 | Mobile-friendly REST refinement, OpenAPI docs | ⏳ Planned |

---

## Getting Started

### Prerequisites

- Docker & Docker Compose v2
- Java 21 (for local API development)
- Python 3.11 (for local recognition-service development)
- Maven 3.9+

### Start local infrastructure

```bash
cp .env.example .env
docker compose -f infra/docker/docker-compose.yml up -d
```

### Run the Spring Boot API

```bash
cd apps/smartgym-api
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

### Run the Python recognition service

```bash
cd apps/recognition-service
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8090
```

The recognition service will be available at `http://localhost:8090`.

---

## Documentation

| Document | Description |
|---|---|
| [Architecture Overview](docs/architecture/overview.md) | Modular monolith, hexagonal boundaries, tech decisions |
| [Domain Modules](docs/architecture/domain-modules.md) | DDD-inspired module breakdown |
| [Face Recognition Design](docs/architecture/face-recognition-design.md) | Python service design and integration contract |
| [MQTT Topic Conventions](docs/mqtt/topic-conventions.md) | Topic naming, versioning, and example payloads |

---

## Contributing

This project follows conventional commits and the branching strategy described in the architecture docs. Please open an issue before submitting large pull requests.

---

## License

MIT © SmartGym IoT Platform contributors
