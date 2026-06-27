package dev.blumek.party.relationships.application;

import dev.blumek.party.relationships.domain.Relationship;

public final class RelationshipSummaries {

    private RelationshipSummaries() {
    }

    public static RelationshipSummary of(final Relationship relationship) {
        return new RelationshipSummary(
                relationship.id().asString(),
                relationship.from().party().asString(),
                relationship.from().role().asString(),
                relationship.to().party().asString(),
                relationship.to().role().asString(),
                relationship.type().asString(),
                relationship.validity().from(),
                relationship.validity().to());
    }
}
