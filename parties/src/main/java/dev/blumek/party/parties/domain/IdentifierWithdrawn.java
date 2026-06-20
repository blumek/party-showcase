package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.DomainEvent;

public record IdentifierWithdrawn(PartyId partyId, OfficialIdentifier identifier) implements DomainEvent {
}
