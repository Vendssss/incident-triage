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

### Build and test

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
