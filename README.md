# party-showcase

A Party Management service built around the **Party archetype**: people and organizations, the roles
they play, their addresses and capabilities, and the relationships between them. It's a modular Spring
Boot application backed by PostgreSQL.

## What is the Party archetype?

A *party* is a person or an organization. Basically any real-world entity a business needs to deal with.
The Party archetype comes from Arlow & Neustadt's analysis-pattern work, and the idea is simple:
customers, suppliers, employees, members and vendors aren't really different things. They're the same
parties playing different roles. So you model the party once and attach the roles, contact details,
capabilities and relationships on top of it.

### The problem it solves

Most systems re-invent the same entity over and over. There's a `customers` table here, a `suppliers`
table there, an `employees` table somewhere else, and they all describe the same real people and
companies. They duplicate the same name, address and identifier columns, and none of them know about each
other. The day a customer turns out to also be a supplier, you've got two disconnected records and no
single view of who that entity actually is.

The archetype avoids this by separating *who someone is* (a stable Party) from *what they're doing for
you right now* (a Role they play). A party can play many roles at the same time, and pick up or drop them
over time, without ever being duplicated.

## What it does

The service models the party once and layers everything else on top of it:

- register **people** and **organizations** (companies and organization units), each with the official
  identifiers that prove who they are: tax numbers for organizations, passport and national numbers for
  people;
- attach the **roles** a party plays (customer, supplier, employer, and so on). A single party can play
  many, and you assign or relinquish them independently;
- keep an **address book** of contact points (email, phone, website, postal), each with a *purpose*
  (residence, workplace, billing) and a *validity period*, so old and current addresses can live side by
  side;
- describe a **capability portfolio**, meaning what a party is able to do, scoped along several
  dimensions (volume, area, schedule, grade) and matched against a stated need;
- record directional **relationships** between parties, like employment, as from/to endpoints with a role
  on each side and a validity period, queryable from either end.

Addresses, capabilities and relationships are all **time-bound**. Each one carries an effective period,
so the model captures not just where things stand today but how they change over time.

Every concern lives in its own module, which keeps the model cohesive and the boundaries explicit instead
of collapsing into one tangled "user" table.

This is a re-implementation of the *pattern*, not a copy of any reference codebase.

## Modules

| Module | Responsibility |
|---|---|
| `shared` | Kernel: functional `Result`, `Version`, `OwnerId`, domain events, guards |
| `parties` | Party aggregate: people, organizations, roles, official identifiers |
| `addresses` | `AddressBook` aggregate: email, phone, website, postal contact points |
| `capabilities` | `CapabilityPortfolio` aggregate: scoped capabilities a party can do |
| `relationships` | `RelationshipLedger` aggregate: directional links between parties |
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

Failures come back as RFC-7807 `application/problem+json` responses (status, detail and a stable
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
