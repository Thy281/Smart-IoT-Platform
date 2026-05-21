from enum import Enum

from pydantic import BaseModel, Field


class EnrolmentStatus(str, Enum):
    ENROLLED = "ENROLLED"
    FAILED = "FAILED"
    ALREADY_ENROLLED = "ALREADY_ENROLLED"


class EnrolRequest(BaseModel):
    member_id: str = Field(..., description="Member UUID")
    photo_b64: str = Field(..., description="Base64-encoded JPEG photo")


class EnrolResponse(BaseModel):
    status: EnrolmentStatus
    member_id: str
    embedding_version: str = "v1"
