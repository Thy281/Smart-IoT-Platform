from fastapi import APIRouter
from app.schemas.enrolment import EnrolRequest, EnrolResponse, EnrolmentStatus
from app.service.enrolment_service import EnrolmentService

router = APIRouter()
_service = EnrolmentService()


@router.post("/enrol", response_model=EnrolResponse)
async def enrol(request: EnrolRequest) -> EnrolResponse:
    """
    Accept a base64-encoded photo and store the face embedding for a member.
    At MVP this is a stub; wire in the real embedding pipeline when ready.
    """
    return await _service.enrol(request)
