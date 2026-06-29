package dev.blumek.party.relationships.application;

import java.time.LocalDate;

public record RelationshipSummary(String id, String fromParty, String fromRole, String toParty, String toRole,
        String type, LocalDate validFrom, LocalDate validTo) {
}
