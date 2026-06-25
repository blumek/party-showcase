package dev.blumek.party.relationships.domain;

import java.util.Optional;

@FunctionalInterface
public interface RelationshipPolicy {

    RelationshipPolicy DEFAULT = permitAll();

    Optional<RelationshipError> check(Endpoint from, Endpoint to, RelationshipType type);

    static RelationshipPolicy permitAll() {
        return (from, to, type) -> Optional.empty();
    }
}
