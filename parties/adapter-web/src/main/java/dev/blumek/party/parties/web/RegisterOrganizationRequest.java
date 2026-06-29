package dev.blumek.party.parties.web;

import jakarta.validation.constraints.NotBlank;

public record RegisterOrganizationRequest(@NotBlank String name) {
}
