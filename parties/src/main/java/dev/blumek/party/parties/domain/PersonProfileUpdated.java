package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.DomainEvent;

public record PersonProfileUpdated(PartyId partyId, PersonProfile profile) implements DomainEvent {
}
