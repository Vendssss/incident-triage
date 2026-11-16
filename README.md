# Incident Triage

A REST API (with a React frontend, in progress) for triaging security incidents and alerts:
analysts log incidents, leads assign and prioritize them, and every change is recorded in an
audit trail.

> **Status:** early development. See [docs/PROGRESS.md](docs/PROGRESS.md) for the roadmap.

## Features

- **Role-based access** ✅ — `ANALYST`, `LEAD` and `ADMIN` roles, enforced with Spring Security and JWT
- **Incidents** — create, view, update and search incidents with a severity level
  (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`)
- **Status workflow** — enforced transitions, e.g. `OPEN → TRIAGED → IN_PROGRESS → RESOLVED → CLOSED`
- **Assignment** — leads assign incidents to analysts
- **Comments** — investigation notes on each incident
- **Audit log** — who changed what, and when
- **Web UI** — React + TypeScript client with role-based views

## Tech stack

| Layer      | Technology                                  |
|------------|---------------------------------------------|
| Language   | Java 21                                     |
| Framework  | Spring Boot 4 (Web MVC, Data JPA, Security) |
| Database   | PostgreSQL, schema versioned with Flyway    |
| Auth       | JWT                                         |
| Testing    | JUnit 5, Mockito                            |
| Build      | Maven (wrapper included)                    |
| Frontend   | React, TypeScript                           |

## Getting started

### Prerequisites

- JDK 21 (`JAVA_HOME` must point to it)
- PostgreSQL 15 or newer

### Database setup

Create a dedicated database and role (run as a PostgreSQL superuser, one statement at a time):

```sql
CREATE ROLE triage_app WITH LOGIN PASSWORD '<choose-a-password>';
CREATE DATABASE incident_triage OWNER triage_app;
```

### Configuration

Secrets are provided through environment variables and are never stored in the repository.

| Variable | Required | Default | Purpose |
|----------|----------|---------|---------|
| `TRIAGE_DB_PASSWORD` | yes | — | Password of the `triage_app` role |
| `TRIAGE_JWT_SECRET` | yes | — | Base64-encoded HMAC key for signing tokens, at least 256 bits |
| `TRIAGE_ADMIN_PASSWORD` | first run | — | Creates the initial `ADMIN` account when no users exist (min. 12 chars) |
| `TRIAGE_DB_URL` | no | `jdbc:postgresql://localhost:5432/incident_triage` | JDBC URL |
| `TRIAGE_DB_USERNAME` | no | `triage_app` | Database role |
| `TRIAGE_ADMIN_USERNAME` | no | `admin` | Username of the initial admin |
| `TRIAGE_ADMIN_EMAIL` | no | `admin@incident-triage.local` | Email of the initial admin |

Generate a JWT secret in PowerShell:

```powershell
[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

or with OpenSSL: `openssl rand -base64 32`.

Tables are created automatically on startup by Flyway migrations in
`src/main/resources/db/migration`.

### Build and test

Tests start the full application against the configured database (each test rolls back its data), so PostgreSQL must be running and `TRIAGE_DB_PASSWORD` set.

```bash
./mvnw clean test
```

On Windows PowerShell, use `.\mvnw clean test`.

### Run

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. Health check: `GET /actuator/health`.

## API

All endpoints except login and the health check require `Authorization: Bearer <token>`.
Errors use the RFC 9457 problem format (`application/problem+json`).

| Method | Path | Access | Description |
|--------|------|--------|-------------|
| `POST` | `/api/auth/login` | public | Exchange username and password for an access token |
| `GET` | `/api/users/me` | any role | Current user's profile |
| `GET` | `/api/users` | `LEAD`, `ADMIN` | List users |
| `GET` | `/api/users/{id}` | `LEAD`, `ADMIN` | Get one user |
| `POST` | `/api/users` | `ADMIN` | Create a user |

Example:

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"<your-admin-password>"}'
```

## Project structure

```
src/main/java/com/vendiola/triage/   application code
src/main/resources/                  configuration
src/test/java/com/vendiola/triage/   tests
docs/                                architecture and project notes
```

## License

[MIT](LICENSE)
