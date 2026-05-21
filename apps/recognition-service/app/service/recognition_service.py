import time

from app.schemas.recognition import RecognizeRequest, RecognizeResponse, RecognitionStatus


class RecognitionService:
    """
    Stub recognition service.
    Replace the body of `recognize` with real OpenCV + face_recognition logic.
    """

    async def recognize(self, request: RecognizeRequest) -> RecognizeResponse:
        start = time.monotonic_ns()

        # TODO: implement real face detection and embedding matching
        # Steps:
        #   1. Decode request.frame_b64 from base64 to bytes
        #   2. cv2.imdecode to numpy array
        #   3. face_recognition.face_locations() to detect faces
        #   4. face_recognition.face_encodings() to get 128-dim embedding
        #   5. Compare against enrolled embeddings using cosine distance
        #   6. Return MATCH / NO_MATCH / NO_FACE accordingly

        elapsed_ms = (time.monotonic_ns() - start) // 1_000_000
        return RecognizeResponse(
            status=RecognitionStatus.NO_FACE,
            member_id=None,
            confidence=None,
            processing_ms=elapsed_ms,
        )
