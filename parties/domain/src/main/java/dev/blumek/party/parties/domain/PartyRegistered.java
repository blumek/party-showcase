package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.DomainEvent;

record PartyRegistered(PartyId partyId) implements DomainEvent {
}
