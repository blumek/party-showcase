package dev.blumek.party.relationships.application;

import dev.blumek.party.relationships.domain.RelationshipId;
import dev.blumek.party.relationships.domain.RelationshipPeriod;
import dev.blumek.party.relationships.domain.RelationshipType;
import dev.blumek.party.relationships.domain.Role;
import dev.blumek.party.shared.OwnerId;

public record EstablishRelationship(OwnerId from, OwnerId to, Role fromRole, Role toRole,
        RelationshipId id, RelationshipType type, RelationshipPeriod validity) {
}
