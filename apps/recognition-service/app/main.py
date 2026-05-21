from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.health import router as health_router
from app.api.recognition import router as recognition_router
from app.api.enrolment import router as enrolment_router
from app.core.config import settings

app = FastAPI(
    title="SmartGym Recognition Service",
    description="Face recognition microservice for SmartGym IoT Platform",
    version="0.1.0",
    docs_url="/docs",
    redoc_url="/redoc",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.allowed_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(health_router, tags=["health"])
app.include_router(recognition_router, prefix="/api/v1", tags=["recognition"])
app.include_router(enrolment_router, prefix="/api/v1", tags=["enrolment"])
