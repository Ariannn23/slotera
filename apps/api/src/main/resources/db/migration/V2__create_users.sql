CREATE TABLE users (
    id UUID PRIMARY KEY,
    issuer VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    email VARCHAR(320) NOT NULL,
    email_verified BOOLEAN NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_issuer_subject_unique UNIQUE (issuer, subject),
    CONSTRAINT users_status_valid CHECK (status IN ('ACTIVE', 'INACTIVE'))
);
