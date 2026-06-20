package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.DomainEvent;

public record RoleAssigned(PartyId partyId, Role role) implements DomainEvent {
}
