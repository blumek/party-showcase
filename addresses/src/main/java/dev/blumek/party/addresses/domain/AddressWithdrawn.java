package dev.blumek.party.addresses.domain;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.OwnerId;

record AddressWithdrawn(OwnerId owner, AddressId addressId, ContactKind kind) implements DomainEvent {
}
