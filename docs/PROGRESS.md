# Progress

Tracks where the project stands so work can resume on any machine.

**Last updated:** 2026-11-10
**Current milestone:** 0 — Repo hygiene

## Milestones

| # | Milestone | Status |
|---|-----------|--------|
| 0 | Repo hygiene | In progress |
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

### Remaining
- [ ] Rename package `com.vendiola.incidentTriage` → `com.vendiola.triage` (lowercase)
- [ ] Replace leftover template names: artifactId `demo`, `spring.application.name=demo`,
      `DemoApplicationTests`, `IncidentHubApplication`
- [ ] Rewrite `README.md` for the security incident triage scope
- [ ] Add `docs/ARCHITECTURE.md` and `docs/GLOSSARY.md`

## Decisions
- **Package name `com.vendiola.triage`.** All lowercase per Java convention, and a genuinely
  different name avoids Git's case-insensitive rename problem on Windows.
- **Dedicated database role.** The app connects as its own least-privilege PostgreSQL role,
  not the `postgres` superuser. Credentials come from environment variables.

## Next up
Finish Milestone 0, then Milestone 1: add Spring Data JPA, the PostgreSQL driver and
Flyway, and create the first versioned migration for users and incidents.

## Environment notes
- JDK 21 (`JAVA_HOME` must point to it; `.\mvnw -v` shows which JDK Maven uses)
- PostgreSQL 18 running locally (`psql` is not on PATH)
- Node 20 for the frontend
- Database credentials will be read from environment variables, never committed
