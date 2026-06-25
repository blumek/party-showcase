package dev.blumek.party.relationships.domain;

import dev.blumek.party.shared.OwnerId;

import static dev.blumek.party.shared.Guards.require;

public record Relationship(RelationshipId id, Endpoint from, Endpoint to, RelationshipType type,
        RelationshipPeriod validity) {

    public Relationship {
        require(id != null, "Relationship requires an id");
        require(from != null, "Relationship requires a from endpoint");
        require(to != null, "Relationship requires a to endpoint");
        require(type != null, "Relationship requires a type");
        require(validity != null, "Relationship requires a validity period");
    }

    public boolean isActiveNow() {
        return validity.isActiveNow();
    }

    public boolean involves(final OwnerId party) {
        require(party != null, "Party cannot be null");
        return from.party().equals(party) || to.party().equals(party);
    }

    public boolean differsFrom(final Relationship other) {
        return !from.equals(other.from) || !to.equals(other.to)
                || !type.equals(other.type) || !validity.equals(other.validity);
    }
}
