package dev.blumek.party.addresses.domain;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.OwnerId;

public record AddressRecorded(OwnerId owner, AddressId addressId, ContactKind kind) implements DomainEvent {
}
