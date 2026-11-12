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

## Data model (planned)

```
users ──< incidents (reporter_id, assignee_id)
incidents ──< comments
incidents ──< audit_log_entries
```

- **Severity:** `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`
- **Status:** `OPEN → TRIAGED → IN_PROGRESS → RESOLVED → CLOSED`
  (plus `REOPENED` from `RESOLVED`/`CLOSED`)
- **Roles:** `ANALYST`, `LEAD`, `ADMIN`

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
| Package-by-feature | Keeps each feature cohesive; easier to navigate than package-by-layer at scale |
| DTOs at the API boundary | Entities can change without breaking the API; avoids leaking internal fields |
| Maven wrapper | Everyone and every build server uses the same Maven version |
