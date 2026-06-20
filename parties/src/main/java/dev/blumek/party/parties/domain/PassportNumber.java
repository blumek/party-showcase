package dev.blumek.party.parties.domain;

import java.util.Locale;

import dev.blumek.party.shared.Guards;

public record PassportNumber(String value) implements OfficialIdentifier {

    public PassportNumber {
        value = canonical(value);
    }

    private static String canonical(final String raw) {
        final var upper = Guards.requireText(raw, "Passport number cannot be blank")
                .strip()
                .toUpperCase(Locale.ROOT);
        Guards.require(upper.matches("[A-Z]{2}\\d{7}"),
                "Passport number must be two letters followed by seven digits");
        return upper;
    }

    @Override
    public IdentifierKind kind() {
        return IdentifierKind.PASSPORT;
    }
}
