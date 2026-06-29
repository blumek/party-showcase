package dev.blumek.party.capabilities.domain;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.OwnerId;

record CapabilityRevised(OwnerId owner, CapabilityId capabilityId, CapabilityKind kind) implements DomainEvent {
}
