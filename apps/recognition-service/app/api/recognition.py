from fastapi import APIRouter
from app.schemas.recognition import RecognizeRequest, RecognizeResponse, RecognitionStatus
from app.service.recognition_service import RecognitionService

router = APIRouter()
_service = RecognitionService()


@router.post("/recognize", response_model=RecognizeResponse)
async def recognize(request: RecognizeRequest) -> RecognizeResponse:
    """
    Accept a base64-encoded JPEG frame and return a recognition result.
    At MVP this is a stub; wire in the real recognition pipeline when ready.
    """
    return await _service.recognize(request)
