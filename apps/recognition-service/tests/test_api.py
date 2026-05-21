import pytest
from httpx import AsyncClient, ASGITransport
from app.main import app


@pytest.mark.asyncio
async def test_health_returns_ok():
    async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as client:
        response = await client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}


@pytest.mark.asyncio
async def test_recognize_stub_returns_no_face():
    payload = {
        "frame_b64": "bm9mcmFtZQ==",  # minimal valid base64
        "device_id": "cam-001",
        "timestamp": "2025-06-01T10:00:00Z",
    }
    async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as client:
        response = await client.post("/api/v1/recognize", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "NO_FACE"
    assert "processing_ms" in data


@pytest.mark.asyncio
async def test_enrol_stub_returns_enrolled():
    payload = {
        "member_id": "member-uuid-001",
        "photo_b64": "bm9waG90bw==",
    }
    async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as client:
        response = await client.post("/api/v1/enrol", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "ENROLLED"
    assert data["member_id"] == "member-uuid-001"
