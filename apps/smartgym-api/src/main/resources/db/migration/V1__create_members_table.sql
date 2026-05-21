-- V1__create_members_table.sql
-- Initial schema: members table

CREATE TABLE members (
    id               UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    full_name        VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL,
    phone            VARCHAR(20),
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    enrolment_status VARCHAR(20)  NOT NULL DEFAULT 'NOT_ENROLLED',
    photo_path       VARCHAR(500),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT members_email_unique UNIQUE (email),
    CONSTRAINT members_status_check
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    CONSTRAINT members_enrolment_status_check
        CHECK (enrolment_status IN ('NOT_ENROLLED', 'ENROLLED', 'FAILED'))
);

CREATE INDEX idx_members_email ON members (email);
CREATE INDEX idx_members_status ON members (status);
