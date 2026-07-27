# ATS — AGENTS.md

## Quick start

```bash
export GROQ_API_KEY="gsk_..."    # required
gradle bootRun                   # dev: serves backend + frontend on :8080
```

Open `http://localhost:8080` — frontend is Spring Boot static content (`src/main/resources/static/`).

**By default** uses H2 file-based database (PostgreSQL compatibility mode) at `./data/ats`.  
Set `SPRING_PROFILES_ACTIVE=prod` (and the env vars below) to connect to real PostgreSQL.

## Build & run

- **No Gradle wrapper** — `gradle` must be on PATH.
- **Java 26** required (toolchain in `build.gradle`).
- Spring Boot 4.0.6 — API differs from 3.x in spots.
- `gradle bootRun` (dev) / `gradle bootJar` (production JAR).
- `gradle build` produces a **plain JAR** without the main class — use `gradle bootJar` instead.
- **No CI, no pre-commit hooks, no linter/formatter configured.**

## Configuration

All config via env vars. `application.properties` handles only DB connection — do not add other Spring properties there unless they also use env-var substitution.

| Env var | Default | Purpose |
|---|---|---|
| `GROQ_API_KEY` | *(required)* | Groq LLM API key |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `ats` | PostgreSQL database name |
| `DB_USER` | `ats` | PostgreSQL user |
| `DB_PASSWORD` | `ats` | PostgreSQL password |

These env vars only take effect with `SPRING_PROFILES_ACTIVE=prod`. By default the app runs with the `dev` profile using H2.

## Database

**By default** uses H2 file-based database (PostgreSQL compatibility mode) at `./data/ats`.  
For production or Docker, set `SPRING_PROFILES_ACTIVE=prod` to connect to PostgreSQL.

PostgreSQL-backed persistence via Spring Data JPA + Hibernate. Schema auto-created on startup (`ddl-auto=update`).

Tables:
- `analysis_results` — one row per CV analysis, storing scores, matched/missing skill lists, and the full candidate/job JSON payloads.

`AnalysisResultRepository` (Spring Data JPA) exposes basic CRUD + `findAllByOrderByCreatedAtDesc()`.

## Docker

- `docker-compose.yml` — full stack: PostgreSQL, backend.
- `Dockerfile.backend` — multi-stage: `gradle bootJar`, runtime `eclipse-temurin:26-jre`. Port 8080.
- `Dockerfile.frontend` — nginx serving static files (optional, backend serves frontend directly on `:8080`).
- `nginx.conf` — proxy config for the optional frontend container.
- `docker-up.sh` — fallback script if `docker compose` is unavailable.

Published image: `bldmhd/ats-backend:latest` on Docker Hub.

```bash
GROQ_API_KEY="gsk_..." docker compose up --build
```

Opens at `http://localhost:8080` — Spring Boot serves both the API and the static frontend.

If `docker compose` is not installed, run the fallback script instead:
```bash
GROQ_API_KEY="gsk_..." ./docker-up.sh
```

To push an updated image to Docker Hub:
```bash
sudo docker login
sudo docker compose build backend
sudo docker push bldmhd/ats-backend:latest
```

## Tests

**`src/test/java/` is empty.** No test runner or framework. Do not assume any.

## Architecture

Single-module Spring Boot app (`com.bld.ats`) with an HTML/CSS/JS frontend at `src/main/resources/static/`.

`POST /api/v1/cv/analyze` — multipart form with `cvFile` (PDF) + `jobDescription` (string).

Flow: `AtsController` → `AtsProcessingService` → AI extraction via Groq API → scoring → persist to H2/PostgreSQL.

- **AI extraction**: `CandidateInfoExtractor` / `JobInfoExtractor` extend `InfoExtractor<T>`, calling `https://api.groq.com/openai/v1/chat/completions` with `meta-llama/llama-4-scout-17b-16e-instruct`. API key from `GROQ_API_KEY` env var (read per-instance in `InfoExtractor`). No retries. Returns `null` on failure.
- **Scoring strategies**: `WeightedPriorityStrategy` (mandatory 70% / nice-to-have 30%) and `KeywordMatchStrategy`. Currently wired: `KeywordMatchStrategy` in `AtsProcessingService`. Change at construction site to switch.
- **Input**: PDF only (Apache PDFBox 3).
- **Controller**: `@CrossOrigin(origins = "*")`, injects `AtsProcessingService` via constructor.
- **Service layer**: `AtsProcessingService` is a `@Service` with `AnalysisResultRepository` injected via constructor — persists each `DetailedScore` after computation.
- **DTOs**: `DetailedScore` is a Java `record`; model classes (`Candidate`, `Job`) are POJOs with record-style accessors.
- **Model pattern**: All model classes use `@JsonAutoDetect(fieldVisibility = ANY)` + `@JsonIgnoreProperties(ignoreUnknown = true)` so Jackson serializes/deserializes private fields directly without getters/setters. Nested types are static inner classes.

## Gotchas

- `JobInfoExtractor` creates its own local `ObjectMapper` (line 19) instead of using the inherited one — different instance from `CandidateInfoExtractor`.
- If the Groq API call fails, the service returns `null` and the controller returns 400. No fallback or caching.
- Persistence failure (JSON serialization error) is logged but does **not** fail the HTTP response — the analysis result is still returned to the client.
- **`README.md` is stale** — it references a non-existent `application.properties`, Java 17+, and the wrong directory name. Trust `AGENTS.md` over `README.md`.
