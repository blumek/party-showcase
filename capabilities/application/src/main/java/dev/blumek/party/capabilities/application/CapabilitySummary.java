package dev.blumek.party.capabilities.application;

import java.time.LocalDate;
import java.util.Set;

public record CapabilitySummary(
        String id,
        String owner,
        String kind,
        Set<ScopeSummary> scopes,
        LocalDate validFrom,
        LocalDate validTo) {

    public record ScopeSummary(String dimension, String value) {
    }
}
