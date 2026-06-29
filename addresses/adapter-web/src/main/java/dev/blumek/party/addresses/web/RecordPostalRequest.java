package dev.blumek.party.addresses.web;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import dev.blumek.party.addresses.domain.AddressPurpose;

public record RecordPostalRequest(
        String addressId,
        @NotEmpty Set<AddressPurpose> purposes,
        LocalDate validFrom,
        LocalDate validTo,
        @NotBlank String line1,
        String line2,
        @NotBlank String city,
        @NotBlank String postalCode,
        @NotBlank String country) {
}
