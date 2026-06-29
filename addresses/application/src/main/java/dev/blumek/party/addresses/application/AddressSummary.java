package dev.blumek.party.addresses.application;

import java.time.LocalDate;
import java.util.Set;

public record AddressSummary(
        String id,
        String kind,
        String value,
        Set<String> purposes,
        LocalDate validFrom,
        LocalDate validTo,
        Postal postal) {

    public record Postal(String line1, String line2, String city, String postalCode, String country) {
    }
}
