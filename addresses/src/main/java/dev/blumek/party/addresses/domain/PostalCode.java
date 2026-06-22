package dev.blumek.party.addresses.domain;

import java.util.Locale;

import static dev.blumek.party.shared.Guards.requireText;

public record PostalCode(String value) {

    public PostalCode {
        value = requireText(value, "Postal code cannot be blank").strip().toUpperCase(Locale.ROOT);
    }
}
