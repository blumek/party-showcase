package dev.blumek.party.parties.web;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleRequest(@NotBlank String name) {
}
