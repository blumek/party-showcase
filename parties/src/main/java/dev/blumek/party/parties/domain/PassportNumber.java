package dev.blumek.party.parties.domain;

import static dev.blumek.party.shared.Guards.require;
import static dev.blumek.party.shared.Guards.requireText;
import static java.util.Locale.ROOT;

public record PassportNumber(String value) implements OfficialIdentifier {

    public PassportNumber {
        value = canonical(value);
    }

    private static String canonical(final String raw) {
        final var upper = requireText(raw, "Passport number cannot be blank")
                .strip()
                .toUpperCase(ROOT);
        require(upper.matches("[A-Z]{2}\\d{7}"),
                "Passport number must be two letters followed by seven digits");
        return upper;
    }

    @Override
    public IdentifierKind kind() {
        return IdentifierKind.PASSPORT;
    }
}
