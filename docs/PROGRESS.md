# Progress

Tracks where the project stands and what comes next.

**Last updated:** 2026-11-16
**Current milestone:** 2 — Auth / RBAC

## Milestones

| # | Milestone | Status |
|---|-----------|--------|
| 0 | Repo hygiene | Done |
| 1 | DB schema (versioned migrations) | Done |
| 2 | Auth / RBAC (Spring Security + JWT) | Not started |
| 3 | Incident CRUD + status workflow | Not started |
| 4 | Assignment, comments, audit log | Not started |
| 5 | Backend tests (JUnit 5 + Mockito) | Not started |
| 6 | Frontend (React + TypeScript) | Not started |

## Milestone 0 — Repo hygiene

### Done
- Moved the Spring Boot project from a nested `demo/` folder to the repo root
- Renamed the GitHub repository `incidentHub` → `incident-triage`; remote URL updated
- Rewrote `.gitignore` with grouped sections (Maven, Java/JVM, logs, IntelliJ, VS Code, OS)
- Removed the committed `target/` build output from version control
  (it contained a copied `application.properties`)
- Fixed the local toolchain: `JAVA_HOME` now points to JDK 21, matching `pom.xml`
- Renamed package `com.vendiola.incidentTriage` → `com.vendiola.triage` (lowercase)
- Replaced template names: `IncidentTriageApplication`, `IncidentTriageApplicationTests`,
  artifactId and `spring.application.name` `incident-triage`; removed empty POM placeholders
- Rewrote `README.md` for the security incident triage scope
- Added `docs/ARCHITECTURE.md`

## Milestone 1 — DB schema

### Done
- Added Spring Data JPA, Flyway and the PostgreSQL driver
- Database connection configured from environment variables (password has no default)
- Created `incident_triage` database and dedicated `triage_app` role
- Migration `V1`: `users` and `incidents` tables with foreign keys, `CHECK` constraints,
  indexes and an optimistic-locking `version` column

## Decisions
- **Package name `com.vendiola.triage`.** All lowercase per Java convention, and a genuinely
  different name avoids Git's case-insensitive rename problem on Windows.
- **Dedicated database role.** The app connects as its own least-privilege PostgreSQL role,
  not the `postgres` superuser. Credentials come from environment variables.

## Next up
Milestone 2: `User` entity and repository, BCrypt password hashing, login endpoint that
issues a JWT, and a security filter that validates it and enforces roles.

## Environment notes
- JDK 21 (`JAVA_HOME` must point to it; `.\mvnw -v` shows which JDK Maven uses)
- PostgreSQL 18 running locally (`psql` is not on PATH)
- Node 20 for the frontend
- Database credentials will be read from environment variables, never committed
