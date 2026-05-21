from enum import Enum
from typing import Optional

from pydantic import BaseModel, Field


class RecognitionStatus(str, Enum):
    MATCH = "MATCH"
    NO_MATCH = "NO_MATCH"
    NO_FACE = "NO_FACE"


class RecognizeRequest(BaseModel):
    frame_b64: str = Field(..., description="Base64-encoded JPEG frame")
    device_id: str = Field(..., description="Source device UUID")
    timestamp: str = Field(..., description="ISO-8601 capture timestamp")


class RecognizeResponse(BaseModel):
    status: RecognitionStatus
    member_id: Optional[str] = None
    confidence: Optional[float] = None
    processing_ms: int
