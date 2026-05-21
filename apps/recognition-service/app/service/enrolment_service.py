import time

from app.schemas.enrolment import EnrolRequest, EnrolResponse, EnrolmentStatus


class EnrolmentService:
    """
    Stub enrolment service.
    Replace with real embedding extraction and storage logic.
    """

    async def enrol(self, request: EnrolRequest) -> EnrolResponse:
        # TODO: implement real enrolment
        # Steps:
        #   1. Decode request.photo_b64 from base64 to bytes
        #   2. Detect face in photo
        #   3. Extract 128-dim embedding
        #   4. Save embedding to data/embeddings/{member_id}.json
        #   5. Return ENROLLED status

        return EnrolResponse(
            status=EnrolmentStatus.ENROLLED,
            member_id=request.member_id,
            embedding_version="v1",
        )
