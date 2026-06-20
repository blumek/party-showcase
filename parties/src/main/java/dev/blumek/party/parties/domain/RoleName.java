package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.Guards;

public record RoleName(String value) {

    public RoleName {
        value = Guards.requireText(value, "Role name cannot be blank").strip();
    }
}
