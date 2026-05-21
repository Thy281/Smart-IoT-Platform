from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    app_name: str = "SmartGym Recognition Service"
    debug: bool = False
    allowed_origins: list[str] = ["http://localhost:8080"]

    # Recognition thresholds
    recognition_confidence_threshold: float = 0.85
    embeddings_dir: str = "data/embeddings"

    # Internal API key (set in production)
    internal_api_key: str = ""

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")


settings = Settings()
