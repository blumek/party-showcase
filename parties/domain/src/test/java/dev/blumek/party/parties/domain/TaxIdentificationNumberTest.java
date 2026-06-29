package dev.blumek.party.parties.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class TaxIdentificationNumberTest {

    @Test
    void reportsTheTaxKind() {
        var actualIdentifier = new TaxIdentificationNumber("1234567890");

        thenKindIs(actualIdentifier, IdentifierKind.TAX);
    }

    private void thenKindIs(final OfficialIdentifier identifier, final IdentifierKind expected) {
        assertThat(identifier.kind()).isEqualTo(expected);
    }

    @Test
    void stripsSeparatorsToACanonicalValue() {
        var actualIdentifier = new TaxIdentificationNumber(" 123-456-7890 ");

        thenValueIs(actualIdentifier, "1234567890");
    }

    private void thenValueIs(final OfficialIdentifier identifier, final String expected) {
        assertThat(identifier.value()).isEqualTo(expected);
    }

    @Test
    void rejectsATooShortNumber() {
        var actualThrown = catchThrowable(() -> new TaxIdentificationNumber("12345"));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNonDigitCharacters() {
        var actualThrown = catchThrowable(() -> new TaxIdentificationNumber("12345678AB"));

        thenIllegalArgumentIsThrown(actualThrown);
    }
}
