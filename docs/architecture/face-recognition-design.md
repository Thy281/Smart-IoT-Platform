# Face Recognition Service Design

## Overview

Face recognition is implemented as a **dedicated Python microservice** (`recognition-service`) rather than inside the Java API. This decision is driven by the maturity of the Python computer-vision ecosystem (OpenCV, `face_recognition`, `dlib`, DeepFace) and the desire to keep the JVM process free from native library coupling.

The service exposes a simple HTTP API that the Java `recognition` module calls through its `RecognitionServicePort` output port.

---

## Why a Separate Service?

| Concern | Java Approach | Python Approach (chosen) |
|---|---|---|
| Library ecosystem | Limited, JNI-dependent | Rich (OpenCV, dlib, InsightFace) |
| Deployment flexibility | Tied to JVM | Independent scaling, GPU-enabled containers |
| Model update cycle | Requires API redeploy | Service can update model independently |
| Resource isolation | Shares JVM heap | Dedicated process, optional GPU allocation |

---

## High-Level Flow

```
ESP32-CAM ──MQTT──► smartgym-api
                         │
                         │ (recognition module receives DeviceTelemetryReceivedEvent)
                         │
                         ▼
                  RecognitionService (Java)
                         │
                         │ POST /api/v1/recognize  (HTTP)
                         ▼
                  recognition-service (Python / FastAPI)
                         │
                         │ 1. Decode frame
                         │ 2. Detect faces (OpenCV)
                         │ 3. Extract embeddings (face_recognition / InsightFace)
                         │ 4. Match against enrolled embeddings (cosine similarity)
                         │
                         ▼
                  RecognitionResponse { member_id, confidence }
                         │
                         ▼
                  smartgym-api publishes MemberIdentifiedEvent
                         │
                         ▼
                  attendance module records check-in
```

---

## API Contract

### Endpoint: `POST /api/v1/recognize`

**Request**

```json
{
  "frame_b64": "<base64-encoded JPEG frame>",
  "device_id": "device-uuid-here",
  "timestamp": "2025-06-01T10:30:00Z"
}
```

**Response (match found)**

```json
{
  "status": "MATCH",
  "member_id": "member-uuid-here",
  "confidence": 0.94,
  "processing_ms": 85
}
```

**Response (no match)**

```json
{
  "status": "NO_MATCH",
  "member_id": null,
  "confidence": null,
  "processing_ms": 62
}
```

**Response (no face detected)**

```json
{
  "status": "NO_FACE",
  "member_id": null,
  "confidence": null,
  "processing_ms": 12
}
```

### Endpoint: `POST /api/v1/enrol`

Used during member onboarding to store a face embedding.

**Request**

```json
{
  "member_id": "member-uuid-here",
  "photo_b64": "<base64-encoded JPEG photo>"
}
```

**Response**

```json
{
  "status": "ENROLLED",
  "member_id": "member-uuid-here",
  "embedding_version": "v1"
}
```

### Endpoint: `GET /health`

Returns `200 OK` with `{"status": "ok"}`.

---

## Face Embedding Storage

At MVP, embeddings are stored as JSON files on disk (`data/embeddings/{member_id}.json`). Future work will migrate to a vector store (e.g., pgvector, Faiss, Qdrant).

Embedding schema (per member):

```json
{
  "member_id": "uuid",
  "embedding": [0.123, -0.456, ...],  // 128-dim float array (face_recognition)
  "created_at": "ISO-8601",
  "version": "v1"
}
```

---

## Confidence Threshold

| Threshold | Decision |
|---|---|
| `>= 0.85` | `MATCH` — high confidence, record attendance |
| `0.70 – 0.85` | Implementation-defined; currently treated as `NO_MATCH` |
| `< 0.70` | `NO_MATCH` |

Threshold values will be environment-configurable (`RECOGNITION_CONFIDENCE_THRESHOLD`).

---

## Recognition Pipeline (detail)

```
Frame bytes
    │
    ▼
[OpenCV] Decode JPEG → numpy array
    │
    ▼
[face_recognition] face_locations() → bounding boxes
    │
    ├── 0 faces → return NO_FACE
    │
    ▼
[face_recognition] face_encodings() → 128-dim embedding
    │
    ▼
Load all enrolled embeddings from disk (cached in memory)
    │
    ▼
Compute cosine distance to each enrolled embedding
    │
    ├── min distance < threshold → MATCH (return member_id)
    │
    └── min distance ≥ threshold → NO_MATCH
```

---

## Security Considerations

- The recognition service is **internal only** — it must not be exposed to the public internet
- Communication between `smartgym-api` and `recognition-service` should be over a private Docker network
- Frame data in transit should be treated as sensitive (PII — biometric data)
- At MVP, mTLS or API key header (`X-Internal-Token`) is recommended

---

## Future Enhancements

| Enhancement | Description |
|---|---|
| GPU inference | Replace `face_recognition` with InsightFace for GPU-accelerated recognition |
| Vector store | Migrate embeddings to pgvector or Qdrant for scalable similarity search |
| Async processing | Decouple via message queue (Redis streams or Kafka) |
| Liveness detection | Add anti-spoofing check to prevent photo attacks |
| Multi-face support | Handle frames with multiple faces (e.g., group entry) |
