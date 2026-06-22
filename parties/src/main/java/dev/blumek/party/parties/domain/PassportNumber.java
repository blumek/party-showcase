package dev.blumek.party.parties.domain;

import java.util.Locale;

import static dev.blumek.party.shared.Guards.require;
import static dev.blumek.party.shared.Guards.requireText;

public record PassportNumber(String value) implements OfficialIdentifier {

    public PassportNumber {
        value = canonical(value);
    }

    private static String canonical(final String raw) {
        final var upper = requireText(raw, "Passport number cannot be blank")
                .strip()
                .toUpperCase(Locale.ROOT);
        require(upper.matches("[A-Z]{2}\\d{7}"),
                "Passport number must be two letters followed by seven digits");
        return upper;
    }

    @Override
    public IdentifierKind kind() {
        return IdentifierKind.PASSPORT;
    }
}
