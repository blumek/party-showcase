package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.Guards;

public record NationalIdentificationNumber(String value) implements OfficialIdentifier {

    public NationalIdentificationNumber {
        value = canonical(value);
    }

    private static String canonical(final String raw) {
        final var digits = Guards.requireText(raw, "National identification number cannot be blank")
                .replaceAll("\\s", "");
        Guards.require(digits.matches("\\d{11}"), "National identification number must be exactly 11 digits");
        return digits;
    }

    @Override
    public IdentifierKind kind() {
        return IdentifierKind.NATIONAL;
    }
}
