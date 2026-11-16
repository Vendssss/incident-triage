# Incident Triage

A REST API (with a React frontend, in progress) for triaging security incidents and alerts:
analysts log incidents, leads assign and prioritize them, and every change is recorded in an
audit trail.

> **Status:** early development. See [docs/PROGRESS.md](docs/PROGRESS.md) for the roadmap.

## Planned features

- **Role-based access** — `ANALYST`, `LEAD` and `ADMIN` roles, enforced with Spring Security and JWT
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

Then provide the password through an environment variable. It is never stored in the repository.

| Variable | Required | Default |
|----------|----------|---------|
| `TRIAGE_DB_PASSWORD` | yes | — |
| `TRIAGE_DB_URL` | no | `jdbc:postgresql://localhost:5432/incident_triage` |
| `TRIAGE_DB_USERNAME` | no | `triage_app` |

Tables are created automatically on startup by Flyway migrations in
`src/main/resources/db/migration`.

### Build and test

Tests start the full application, so the database must be running.

```bash
./mvnw clean test
```

On Windows PowerShell, use `.\mvnw clean test`.

### Run

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. Health check: `GET /actuator/health`.

## Project structure

```
src/main/java/com/vendiola/triage/   application code
src/main/resources/                  configuration
src/test/java/com/vendiola/triage/   tests
docs/                                architecture and project notes
```

## License

[MIT](LICENSE)
