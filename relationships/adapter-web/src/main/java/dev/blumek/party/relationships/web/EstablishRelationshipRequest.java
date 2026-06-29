package dev.blumek.party.relationships.web;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record EstablishRelationshipRequest(
        @NotBlank String to,
        @NotBlank String fromRole,
        @NotBlank String toRole,
        @NotBlank String type,
        LocalDate validFrom,
        LocalDate validTo) {
}
