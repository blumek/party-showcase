package dev.blumek.party.relationships.application;

import dev.blumek.party.relationships.domain.RelationshipId;
import dev.blumek.party.shared.OwnerId;

public record TerminateRelationship(OwnerId from, RelationshipId id) {
}
