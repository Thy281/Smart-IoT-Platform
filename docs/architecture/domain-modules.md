# Domain Modules

SmartGym IoT Platform organises its business capabilities into **six DDD-inspired modules**. Each module owns its domain model, use cases, and persistence adapters. Cross-module communication happens via **domain events** or explicit service calls through defined interfaces—never via direct repository access across boundaries.

---

## Module Map

```
com.smartgym/
├── shared/          # Cross-cutting utilities (no domain logic)
├── identity/        # Authentication, JWT, user accounts
├── member/          # Member profiles, photo management
├── device/          # ESP32 device registration and MQTT ingestion
├── attendance/      # Check-in/check-out events and reporting
├── recognition/     # Face recognition orchestration (calls Python service)
└── analytics/       # Aggregated statistics and reporting
```

---

## 1. `shared`

**Purpose:** Provides utilities, base types, and cross-cutting infrastructure. Contains no domain logic.

| Package | Contents |
|---|---|
| `shared.api` | `ApiResponse<T>`, `ErrorResponse`, `PageResponse<T>` |
| `shared.exception` | `GlobalExceptionHandler`, `BusinessException`, `ResourceNotFoundException` |
| `shared.security` | `JwtService`, `JwtAuthFilter`, `SecurityConfig` |
| `shared.config` | `MqttConfig`, `RedisConfig`, `OpenApiConfig` |
| `shared.util` | `UuidUtil`, `ClockUtil` |

**Dependencies:** None (other modules may depend on `shared`).

---

## 2. `identity`

**Purpose:** Manages authentication and authorisation. Issues and validates JWT tokens.

**Key domain concepts:**
- `UserAccount` — system user with role (`ADMIN`, `STAFF`, `MEMBER`)
- `Role` — enum, used in JWT claims and Spring Security authorities

**Primary use cases:**
- `LoginUseCase` — validate credentials, issue JWT pair
- `RefreshTokenUseCase` — rotate refresh token
- `RegisterAccountUseCase` — create staff/admin accounts

**APIs exposed:**
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/register`

**Notes:** Member check-in via face recognition does not use JWT—it goes through the `recognition` module directly.

---

## 3. `member`

**Purpose:** Manages gym member profiles and their associated biometric enrolment status.

**Key domain concepts:**
- `Member` — core aggregate: UUID id, full name, email, phone, status, photo reference
- `MemberStatus` — `ACTIVE`, `INACTIVE`, `SUSPENDED`
- `EnrolmentStatus` — `NOT_ENROLLED`, `ENROLLED`, `FAILED`

**Primary use cases:**
- `RegisterMemberUseCase`
- `UpdateMemberUseCase`
- `GetMemberUseCase`
- `ListMembersUseCase`
- `UploadMemberPhotoUseCase`
- `EnrolMemberFaceUseCase` (triggers recognition service)

**APIs exposed:**
- `POST   /api/v1/members`
- `GET    /api/v1/members`
- `GET    /api/v1/members/{id}`
- `PUT    /api/v1/members/{id}`
- `DELETE /api/v1/members/{id}`
- `POST   /api/v1/members/{id}/enrol`

**Events published:**
- `MemberRegisteredEvent`
- `MemberEnrolledEvent`

---

## 4. `device`

**Purpose:** Manages registered IoT devices (ESP32 sensors, ESP32-CAMs) and processes inbound MQTT telemetry.

**Key domain concepts:**
- `Device` — UUID id, type (`SENSOR`, `CAMERA`), location, status
- `DeviceEvent` — raw MQTT payload parsed into a typed event
- `EquipmentUsage` — derived from sensor events

**Primary use cases:**
- `RegisterDeviceUseCase`
- `ProcessTelemetryUseCase` (driven by MQTT adapter)
- `GetDeviceStatusUseCase`

**APIs exposed:**
- `POST /api/v1/devices`
- `GET  /api/v1/devices`
- `GET  /api/v1/devices/{id}/status`

**MQTT topics consumed:**
- `sg/v1/device/{deviceId}/telemetry`
- `sg/v1/device/{deviceId}/status`

**Events published:**
- `DeviceTelemetryReceivedEvent`

---

## 5. `attendance`

**Purpose:** Records and queries member check-in/check-out events.

**Key domain concepts:**
- `AttendanceRecord` — member ref, check-in time, check-out time, method (`FACE`, `RFID`, `MANUAL`)
- `AttendanceSummary` — VO used for reporting

**Primary use cases:**
- `RecordCheckInUseCase`
- `RecordCheckOutUseCase`
- `GetAttendanceUseCase`
- `GenerateDailySummaryUseCase`

**APIs exposed:**
- `POST /api/v1/attendance/checkin`
- `POST /api/v1/attendance/checkout`
- `GET  /api/v1/attendance`
- `GET  /api/v1/attendance/summary`

**Events consumed:**
- `MemberIdentifiedEvent` (from `recognition`)

---

## 6. `recognition`

**Purpose:** Orchestrates face recognition: receives a frame from a device event, calls the Python recognition service, and publishes the result as a domain event.

**Key domain concepts:**
- `RecognitionRequest` — frame bytes or URL, source device
- `RecognitionResult` — identified member id (nullable), confidence score

**Primary use cases:**
- `SubmitFrameForRecognitionUseCase`

**Output ports:**
- `RecognitionServicePort` — interface; implemented by `HttpRecognitionServiceAdapter`

**Events published:**
- `MemberIdentifiedEvent` (consumed by `attendance`)
- `UnknownPersonDetectedEvent`

---

## 7. `analytics`

**Purpose:** Provides aggregated, read-model data for dashboards and reporting.

**Key domain concepts:**
- `DailyAttendanceStat`
- `EquipmentUsageStat`
- `PeakHourStat`

**Primary use cases:**
- `GetDashboardSummaryUseCase`
- `GetEquipmentUsageReportUseCase`

**APIs exposed:**
- `GET /api/v1/analytics/dashboard`
- `GET /api/v1/analytics/equipment`

**Notes:** Analytics is read-only at MVP; it queries the same database as other modules via dedicated read-model queries. Future work may introduce a separate read store (e.g., TimescaleDB).

---

## Inter-Module Dependency Rules

```
identity  ──►  shared
member    ──►  shared
device    ──►  shared
attendance──►  shared, member (read-only lookup)
recognition──► shared, member (read-only lookup)
analytics ──►  shared
```

**Forbidden:** Direct repository access across module boundaries. If module A needs data from module B, it calls B's application service interface or listens to B's domain events.

---

## Domain Events (summary)

| Event | Publisher | Consumers |
|---|---|---|
| `MemberRegisteredEvent` | `member` | `recognition` (trigger enrolment) |
| `MemberEnrolledEvent` | `member` | `analytics` |
| `MemberIdentifiedEvent` | `recognition` | `attendance` |
| `UnknownPersonDetectedEvent` | `recognition` | `analytics`, future alerting |
| `DeviceTelemetryReceivedEvent` | `device` | `attendance`, `analytics` |
