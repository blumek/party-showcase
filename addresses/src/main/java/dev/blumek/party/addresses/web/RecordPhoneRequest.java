package dev.blumek.party.addresses.web;

import java.time.LocalDate;
import java.util.Set;

import dev.blumek.party.addresses.domain.AddressPurpose;

public record RecordPhoneRequest(
        String addressId,
        Set<AddressPurpose> purposes,
        LocalDate validFrom,
        LocalDate validTo,
        String phone) {
}
