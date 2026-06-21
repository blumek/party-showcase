package dev.blumek.party.addresses.application;

import dev.blumek.party.addresses.domain.AddressId;
import dev.blumek.party.shared.OwnerId;

public record RemoveAddress(OwnerId owner, AddressId addressId) {
}
