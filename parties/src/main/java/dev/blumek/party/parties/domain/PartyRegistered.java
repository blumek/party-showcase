package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.DomainEvent;

public record PartyRegistered(PartyId partyId) implements DomainEvent {
}
