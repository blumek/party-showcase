package dev.blumek.party.addresses.domain;

import static dev.blumek.party.shared.Guards.requireText;
import static java.util.Locale.ROOT;

public record PostalCode(String value) {

    public PostalCode {
        value = requireText(value, "Postal code cannot be blank").strip().toUpperCase(ROOT);
    }
}
