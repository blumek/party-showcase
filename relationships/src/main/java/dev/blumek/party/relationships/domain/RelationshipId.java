package dev.blumek.party.relationships.domain;

import java.util.UUID;

import static dev.blumek.party.shared.Guards.require;

public record RelationshipId(UUID value) {

    public RelationshipId {
        require(value != null, "Relationship id value cannot be null");
    }

    public static RelationshipId of(final String value) {
        return new RelationshipId(UUID.fromString(value));
    }

    public static RelationshipId random() {
        return new RelationshipId(UUID.randomUUID());
    }

    public String asString() {
        return value.toString();
    }
}
