package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.DomainEvent;

record RoleRelinquished(PartyId partyId, Role role) implements DomainEvent {
}
