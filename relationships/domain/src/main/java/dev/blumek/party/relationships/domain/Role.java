package dev.blumek.party.relationships.domain;

import static dev.blumek.party.shared.Guards.requireText;

public record Role(String value) {

    public Role {
        value = requireText(value, "Role cannot be blank").strip();
    }

    public static Role of(final String value) {
        return new Role(value);
    }

    public String asString() {
        return value;
    }
}
