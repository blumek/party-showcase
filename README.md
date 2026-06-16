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

## Run

Docker Compose setup (app + PostgreSQL) is added later in the build.

## Credits

The domain is modelled on the Party archetype pattern described by Arlow & Neustadt in
*Enterprise Patterns and MDA*.
