package dev.blumek.party.parties.application;

import java.util.Set;

public record PartySummary(
        String id,
        String kind,
        String displayName,
        Set<String> roles,
        Set<PartySummary.IdentifierSummary> identifiers) {

    public record IdentifierSummary(String kind, String value) {
    }
}
