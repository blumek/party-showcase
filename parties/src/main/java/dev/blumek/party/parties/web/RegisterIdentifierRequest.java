package dev.blumek.party.parties.web;

import jakarta.validation.constraints.NotBlank;

public record RegisterIdentifierRequest(@NotBlank String kind, @NotBlank String value) {
}
