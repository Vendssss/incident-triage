# Progress

Tracks where the project stands and what comes next.

**Last updated:** 2026-11-13
**Current milestone:** 1 — DB schema

## Milestones

| # | Milestone | Status |
|---|-----------|--------|
| 0 | Repo hygiene | Done |
| 1 | DB schema (versioned migrations) | Not started |
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

## Decisions
- **Package name `com.vendiola.triage`.** All lowercase per Java convention, and a genuinely
  different name avoids Git's case-insensitive rename problem on Windows.
- **Dedicated database role.** The app connects as its own least-privilege PostgreSQL role,
  not the `postgres` superuser. Credentials come from environment variables.

## Next up
Milestone 1: add Spring Data JPA, the PostgreSQL driver and Flyway; create a dedicated
database and role; write the first versioned migration for users and incidents.

## Environment notes
- JDK 21 (`JAVA_HOME` must point to it; `.\mvnw -v` shows which JDK Maven uses)
- PostgreSQL 18 running locally (`psql` is not on PATH)
- Node 20 for the frontend
- Database credentials will be read from environment variables, never committed
