package dev.blumek.party.relationships.domain;

import static dev.blumek.party.shared.Guards.requireText;

public record RelationshipType(String value) {

    public RelationshipType {
        value = requireText(value, "Relationship type cannot be blank").strip();
    }

    public static RelationshipType of(final String value) {
        return new RelationshipType(value);
    }

    public String asString() {
        return value;
    }
}
