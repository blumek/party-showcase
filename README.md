# party-showcase

A Party Management service: a hands-on take on the **Party archetype** — people and organizations,
the roles they play, their addresses and capabilities, and the relationships between them — built as
a modular Spring Boot application backed by PostgreSQL.

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
