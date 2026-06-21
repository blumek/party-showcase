package dev.blumek.party.addresses.application;

import java.util.Set;

import dev.blumek.party.addresses.domain.AddressId;
import dev.blumek.party.addresses.domain.AddressPurpose;
import dev.blumek.party.addresses.domain.ValidityPeriod;
import dev.blumek.party.shared.OwnerId;

public record RecordWebsiteUrl(
        OwnerId owner,
        AddressId addressId,
        Set<AddressPurpose> purposes,
        ValidityPeriod validity,
        String url) {
}
