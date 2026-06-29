package dev.blumek.party.parties.domain;

import static dev.blumek.party.shared.Guards.requireText;

public record RoleName(String value) {

    public RoleName {
        value = requireText(value, "Role name cannot be blank").strip();
    }
}
