package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.DomainEvent;

record OrganizationRenamed(PartyId partyId, LegalName name) implements DomainEvent {
}
