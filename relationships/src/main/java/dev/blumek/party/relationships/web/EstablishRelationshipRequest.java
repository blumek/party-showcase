package dev.blumek.party.relationships.web;

import java.time.LocalDate;

public record EstablishRelationshipRequest(String relationshipId, String to, String fromRole, String toRole,
        String type, LocalDate validFrom, LocalDate validTo) {
}
