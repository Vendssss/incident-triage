CREATE TABLE users (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_users_role CHECK (role IN ('ANALYST', 'LEAD', 'ADMIN'))
);

CREATE TABLE incidents (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    severity    VARCHAR(20)  NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    reporter_id BIGINT       NOT NULL,
    assignee_id BIGINT,
    version     BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    resolved_at TIMESTAMPTZ,

    CONSTRAINT fk_incidents_reporter FOREIGN KEY (reporter_id) REFERENCES users (id),
    CONSTRAINT fk_incidents_assignee FOREIGN KEY (assignee_id) REFERENCES users (id),
    CONSTRAINT chk_incidents_severity
        CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    CONSTRAINT chk_incidents_status
        CHECK (status IN ('OPEN', 'TRIAGED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED', 'REOPENED'))
);

CREATE INDEX idx_incidents_status      ON incidents (status);
CREATE INDEX idx_incidents_severity    ON incidents (severity);
CREATE INDEX idx_incidents_assignee_id ON incidents (assignee_id);
CREATE INDEX idx_incidents_reporter_id ON incidents (reporter_id);
