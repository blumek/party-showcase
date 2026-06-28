package dev.blumek.party.capabilities.web;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record GrantCapabilityRequest(
        String capabilityId,
        @NotBlank String kind,
        @Valid List<ScopeRequest> scopes,
        LocalDate validFrom,
        LocalDate validTo) {
}
