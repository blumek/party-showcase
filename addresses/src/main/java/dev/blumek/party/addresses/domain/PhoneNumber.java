package dev.blumek.party.addresses.domain;

import dev.blumek.party.shared.Guards;

public record PhoneNumber(String value) implements ContactPoint {

    public PhoneNumber {
        value = canonical(value);
    }

    private static String canonical(final String raw) {
        final var trimmed = Guards.requireText(raw, "Phone number cannot be blank").strip();
        final var international = trimmed.startsWith("+");
        final var digits = trimmed.replaceAll("\\D", "");
        Guards.require(digits.matches("\\d{7,15}"), "Phone number must contain 7 to 15 digits");
        return international ? "+" + digits : digits;
    }

    @Override
    public ContactKind kind() {
        return ContactKind.PHONE;
    }
}
