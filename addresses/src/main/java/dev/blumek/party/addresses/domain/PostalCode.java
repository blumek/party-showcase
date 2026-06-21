package dev.blumek.party.addresses.domain;

import java.util.Locale;

import dev.blumek.party.shared.Guards;

public record PostalCode(String value) {

    public PostalCode {
        value = Guards.requireText(value, "Postal code cannot be blank").strip().toUpperCase(Locale.ROOT);
    }
}
