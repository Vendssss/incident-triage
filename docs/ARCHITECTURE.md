# Architecture

Living overview of how the system is built. Updated as the code changes; describes the
current state, not history.

## Overview

```
 React + TypeScript client
          │  HTTPS, JSON, JWT in Authorization header
          ▼
 ┌─────────────────────────────────────────┐
 │ Spring Boot application                 │
 │                                         │
 │  Controller  →  Service  →  Repository  │
 │  (HTTP/DTOs)    (rules)     (JPA)       │
 │                                         │
 │  Spring Security filter chain (JWT)     │
 └─────────────────────────────────────────┘
          │  JDBC
          ▼
     PostgreSQL  (schema managed by Flyway)
```

## Layers

| Layer | Responsibility | Must not |
|-------|----------------|----------|
| **Controller** | Map HTTP requests to service calls; validate input DTOs; return response DTOs and status codes | Contain business rules or touch repositories directly |
| **Service** | Business rules: status transitions, permission checks, audit logging; transaction boundaries | Know about HTTP |
| **Repository** | Database access via Spring Data JPA | Contain business rules |
| **Domain** | JPA entities and enums (`Severity`, `Status`, `Role`) | Be returned directly from controllers |

## Packages

Package-by-feature under `com.vendiola.triage`, so related code stays together as the
project grows.

| Package | Contents | Status |
|---------|----------|--------|
| `com.vendiola.triage` | `IncidentTriageApplication` (entry point) | Done |
| `…triage.incident` | Incident entity, repository, service, controller, DTOs | Planned |
| `…triage.user` | User entity, roles | Planned |
| `…triage.auth` | Login, JWT issuing and validation, security config | Planned |
| `…triage.comment` | Incident comments | Planned |
| `…triage.audit` | Audit log entries | Planned |
| `…triage.common` | Error handling, shared utilities | Planned |

## Data model

Schema is defined only by Flyway migrations in `src/main/resources/db/migration`.
Hibernate runs with `ddl-auto=validate`: it checks entities against the schema but never
changes it.

```
users 1 ──< incidents   (reporter_id: who reported it, required)
users 1 ──< incidents   (assignee_id: who is working on it, optional)
incidents 1 ──< comments            (planned, V2+)
incidents 1 ──< audit_log_entries   (planned, V2+)
```

| Table | Key columns | Notes |
|-------|-------------|-------|
| `users` | `username`, `email` (both unique), `password_hash`, `role`, `enabled` | Stores a BCrypt hash, never the password |
| `incidents` | `title`, `description`, `severity`, `status`, `reporter_id`, `assignee_id`, `version`, `resolved_at` | `version` enables optimistic locking |

Conventions:
- Primary keys are `BIGINT GENERATED ALWAYS AS IDENTITY` (database-generated).
- Timestamps are `TIMESTAMPTZ` (stored in UTC, time-zone aware).
- Enumerated values are `VARCHAR` with a `CHECK` constraint, mapped to Java enums as
  strings, so values are readable in the database and reordering an enum is safe.
- Foreign keys and columns used for filtering (`status`, `severity`, `assignee_id`,
  `reporter_id`) are indexed.

Allowed values:
- **Severity:** `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`
- **Status:** `OPEN → TRIAGED → IN_PROGRESS → RESOLVED → CLOSED`
  (plus `REOPENED` from `RESOLVED`/`CLOSED`)
- **Roles:** `ANALYST`, `LEAD`, `ADMIN`

## Configuration

| Property | Source |
|----------|--------|
| `spring.datasource.password` | `TRIAGE_DB_PASSWORD` environment variable (required, no default) |
| `spring.datasource.url` | `TRIAGE_DB_URL`, defaults to local `incident_triage` |
| `spring.datasource.username` | `TRIAGE_DB_USERNAME`, defaults to `triage_app` |
| `spring.jpa.open-in-view` | `false`: database work stays inside the service layer |

## Security model (planned)

- Stateless authentication with JWT; no server-side sessions.
- Passwords hashed with BCrypt.
- Role checks at the service layer, not only in the UI.
- Secrets (DB password, JWT signing key) come from environment variables, never from
  committed files.
- The application connects to PostgreSQL as a dedicated least-privilege role, not the
  `postgres` superuser.

## Key decisions

| Decision | Reason |
|----------|--------|
| Java 21 + Spring Boot 4 | Current LTS Java; the dominant enterprise stack for the target domain |
| PostgreSQL | Open-source, strong constraints and transactional guarantees |
| Flyway migrations | Schema changes are versioned, reviewable and repeatable across environments |
| `ddl-auto=validate` | Hibernate must never alter a shared database; migrations are the single source of truth |
| Optimistic locking on incidents | Two analysts editing the same incident can't silently overwrite each other |
| Package-by-feature | Keeps each feature cohesive; easier to navigate than package-by-layer at scale |
| DTOs at the API boundary | Entities can change without breaking the API; avoids leaking internal fields |
| Maven wrapper | Everyone and every build server uses the same Maven version |
