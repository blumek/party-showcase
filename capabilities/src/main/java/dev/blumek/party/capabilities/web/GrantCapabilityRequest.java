package dev.blumek.party.capabilities.web;

import java.time.LocalDate;
import java.util.List;

public record GrantCapabilityRequest(
        String capabilityId,
        String kind,
        List<ScopeRequest> scopes,
        LocalDate validFrom,
        LocalDate validTo) {
}
