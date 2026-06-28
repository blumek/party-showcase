# party-showcase

A Party Management service: a hands-on take on the **Party archetype** — people and organizations,
the roles they play, their addresses and capabilities, and the relationships between them — built as
a modular Spring Boot application backed by PostgreSQL.

## What it does

Most systems re-invent the same "person or organization" model badly: a `customers` table here, a
`suppliers` table there, an `employees` table somewhere else — all describing the same real-world
entities, none able to talk to each other. The **Party archetype** solves this by modelling the entity
once (a *party*) and layering everything else on top:

- register **people** and **organizations** (companies, organization units) and the official identifiers
  that prove who they are (tax, passport, national);
- attach the **roles** a party plays (customer, supplier, employer…) — a single party can play many;
- record **addresses** (email, phone, web, postal) and **capabilities** (what a party is able to do);
- link parties to each other through directional **relationships** (e.g. employment) and query them
  both ways.

Each concern is its own module so the model stays cohesive and the boundaries stay explicit, rather than
collapsing into one tangled "user" table.

This is a re-implementation of the *pattern*, not a copy of any reference codebase.

## Modules

| Module | Responsibility |
|---|---|
| `shared` | Kernel: functional result, versioning, domain events |
| `parties` | Party aggregate — people, organizations, roles, registered identifiers |
| `addresses` | Address aggregate — email, phone, web, geographic |
| `capabilities` | Capability aggregate — what a party can do |
| `relationships` | Party relationship aggregate |
| `bootstrap` | Runnable app: HTTP API, configuration, persistence wiring |

## Build

```
./mvnw verify
```

Integration tests spin up PostgreSQL via Testcontainers, so Docker must be running for `verify`.

## Run

### Full stack (app + database)

```
docker compose up --build
```

Brings up PostgreSQL, then the app once the database is healthy; Flyway migrates the schema on boot.

```
curl localhost:8080/actuator/health
curl -X POST localhost:8080/parties/people \
  -H 'Content-Type: application/json' \
  -d '{"given":"Ada","family":"Lovelace","dateOfBirth":"1815-12-10"}'
```

Stop with `docker compose down`, or `docker compose down -v` to also drop the database volume.

### API documentation

Interactive docs are served by the running app:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

Failures are returned as RFC-7807 `application/problem+json` responses (status, detail and a stable
`code`), and request bodies are bean-validated.

### Local development (app on host, database in Docker)

```
docker compose up -d db
./mvnw -pl bootstrap spring-boot:run
```

The `jdbc` profile is active by default and targets `localhost:5432` with the defaults below.

### Configuration

| Variable | Default | Compose value |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/party_showcase` | `jdbc:postgresql://db:5432/party_showcase` |
| `DB_USER` | `party` | `party` |
| `DB_PASSWORD` | `party` | `party` |

App listens on `8080`. Actuator exposes `health` and `info` under `/actuator`.
