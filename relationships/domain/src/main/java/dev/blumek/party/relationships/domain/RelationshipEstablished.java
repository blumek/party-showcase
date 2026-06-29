package dev.blumek.party.relationships.domain;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.OwnerId;

record RelationshipEstablished(OwnerId owner, RelationshipId relationshipId, RelationshipType type)
        implements DomainEvent {
}
