# MQTT Topic Conventions

## Overview

All MQTT topics in SmartGym IoT Platform follow a **versioned, hierarchical naming convention** that identifies the environment, API version, entity type, entity ID, and message type. This ensures forward compatibility as the platform evolves.

---

## Topic Structure

```
sg/{version}/{entity-type}/{entity-id}/{message-type}
```

| Segment | Description | Example |
|---|---|---|
| `sg` | Platform namespace prefix (always `sg`) | `sg` |
| `{version}` | API version, allows non-breaking evolution | `v1` |
| `{entity-type}` | Entity category | `device`, `camera`, `gate` |
| `{entity-id}` | Unique identifier for the entity | `esp32-001` |
| `{message-type}` | Specific message or event type | `telemetry`, `status`, `frame` |

---

## Topic Registry

### Device Telemetry

Published **by ESP32 sensors**. Contains environmental or equipment sensor readings.

```
sg/v1/device/{deviceId}/telemetry
```

**Example payload:**
```json
{
  "device_id": "esp32-001",
  "timestamp": "2025-06-01T10:30:00Z",
  "type": "EQUIPMENT_USAGE",
  "payload": {
    "equipment_id": "treadmill-03",
    "in_use": true,
    "session_start": "2025-06-01T10:25:00Z"
  }
}
```

---

### Device Status

Published **by ESP32 devices** on connect/disconnect or heartbeat.

```
sg/v1/device/{deviceId}/status
```

**Example payload:**
```json
{
  "device_id": "esp32-001",
  "timestamp": "2025-06-01T10:30:00Z",
  "online": true,
  "firmware_version": "1.2.3",
  "rssi": -65,
  "uptime_s": 3600
}
```

**LWT (Last Will and Testament) topic:** same topic, `online: false`.

---

### Camera Frame

Published **by ESP32-CAM** when motion is detected or on a fixed interval.

```
sg/v1/camera/{deviceId}/frame
```

**Example payload:**
```json
{
  "device_id": "cam-entrance-01",
  "timestamp": "2025-06-01T10:30:05Z",
  "frame_b64": "<base64-encoded JPEG>",
  "trigger": "MOTION"
}
```

> **Note:** For bandwidth efficiency, consider using a binary protocol (e.g., CBOR) or storing the frame in object storage and publishing only a URL reference.

---

### Recognition Result (Server → Device)

Published **by smartgym-api** after face recognition completes. Devices can subscribe to trigger physical actions (e.g., open gate, display welcome message).

```
sg/v1/camera/{deviceId}/recognition-result
```

**Example payload:**
```json
{
  "device_id": "cam-entrance-01",
  "timestamp": "2025-06-01T10:30:06Z",
  "status": "MATCH",
  "member_name": "Ana Lima",
  "access_granted": true
}
```

---

### Gate Command (Server → Gate Controller)

Published **by smartgym-api** to control physical gate/turnstile.

```
sg/v1/gate/{deviceId}/command
```

**Example payload:**
```json
{
  "device_id": "gate-main-01",
  "timestamp": "2025-06-01T10:30:06Z",
  "action": "OPEN",
  "duration_ms": 3000
}
```

---

### Alert

Published **by smartgym-api** for operational alerts.

```
sg/v1/alert/{severity}/{alertType}
```

| Severity | Description |
|---|---|
| `info` | Informational events |
| `warning` | Degraded conditions |
| `critical` | Requires immediate attention |

**Example payload:**
```json
{
  "timestamp": "2025-06-01T10:30:10Z",
  "severity": "warning",
  "alert_type": "DEVICE_OFFLINE",
  "message": "Device esp32-001 has gone offline",
  "device_id": "esp32-001"
}
```

---

## QoS Guidelines

| Topic | Direction | QoS | Rationale |
|---|---|---|---|
| `…/telemetry` | Device → Server | 1 | At-least-once, occasional duplicate tolerable |
| `…/status` | Device → Server | 1 | Reliability needed; LWT must also use QoS 1 |
| `…/frame` | Device → Server | 0 | High frequency; best-effort acceptable |
| `…/recognition-result` | Server → Device | 1 | Physical action depends on delivery |
| `…/gate/command` | Server → Device | 2 | Exactly-once; gates must not double-open |
| `…/alert/…` | Server → … | 1 | Important, tolerate rare duplicate |

---

## Retained Messages

- `…/device/{deviceId}/status` — **retained** so new subscribers immediately see the last known device state
- `…/gate/{deviceId}/command` — **not retained** (commands are one-time actions)

---

## Versioning Strategy

- **Minor, backward-compatible changes** (add optional fields to payload): no version bump required
- **Breaking schema changes**: bump version segment (`v1` → `v2`) and run both versions in parallel during transition
- Deprecated topics will be listed in this document with a sunset date before removal

---

## Mosquitto Configuration Notes

Use ACL rules to enforce publish/subscribe access:

```
# Devices may only publish to their own telemetry/status topics
topic write sg/v1/device/%u/telemetry
topic write sg/v1/device/%u/status
topic write sg/v1/camera/%u/frame

# Server (API) may publish commands and read all topics
topic readwrite sg/v1/#
```

(Replace `%u` with the MQTT client username convention, e.g. device serial.)
