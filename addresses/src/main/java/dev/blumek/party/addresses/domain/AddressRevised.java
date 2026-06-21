package dev.blumek.party.addresses.domain;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.OwnerId;

public record AddressRevised(OwnerId owner, AddressId addressId, ContactKind kind) implements DomainEvent {
}
