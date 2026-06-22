package dev.blumek.party.parties.domain;

import static dev.blumek.party.shared.Guards.require;
import static dev.blumek.party.shared.Guards.requireText;

public record TaxIdentificationNumber(String value) implements OfficialIdentifier {

    public TaxIdentificationNumber {
        value = canonical(value);
    }

    private static String canonical(final String raw) {
        final var digits = requireText(raw, "Tax identification number cannot be blank")
                .replaceAll("[\\s-]", "");
        require(digits.matches("\\d{10,14}"), "Tax identification number must be 10 to 14 digits");
        return digits;
    }

    @Override
    public IdentifierKind kind() {
        return IdentifierKind.TAX;
    }
}
