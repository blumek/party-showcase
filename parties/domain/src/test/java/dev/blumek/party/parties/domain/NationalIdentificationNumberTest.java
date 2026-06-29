package dev.blumek.party.parties.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class NationalIdentificationNumberTest {

    @Test
    void reportsTheNationalKind() {
        var actualIdentifier = new NationalIdentificationNumber("44051401458");

        thenKindIs(actualIdentifier, IdentifierKind.NATIONAL);
    }

    private void thenKindIs(final OfficialIdentifier identifier, final IdentifierKind expected) {
        assertThat(identifier.kind()).isEqualTo(expected);
    }

    @Test
    void stripsWhitespaceToACanonicalValue() {
        var actualIdentifier = new NationalIdentificationNumber("440 514 01458");

        thenValueIs(actualIdentifier, "44051401458");
    }

    private void thenValueIs(final OfficialIdentifier identifier, final String expected) {
        assertThat(identifier.value()).isEqualTo(expected);
    }

    @Test
    void rejectsANumberOfTheWrongLength() {
        var actualThrown = catchThrowable(() -> new NationalIdentificationNumber("12345"));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
