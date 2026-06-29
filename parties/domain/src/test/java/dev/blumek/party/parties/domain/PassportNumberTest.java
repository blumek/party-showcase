package dev.blumek.party.parties.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PassportNumberTest {

    @Test
    void reportsThePassportKind() {
        var actualIdentifier = new PassportNumber("AB1234567");

        thenKindIs(actualIdentifier, IdentifierKind.PASSPORT);
    }

    private void thenKindIs(final OfficialIdentifier identifier, final IdentifierKind expected) {
        assertThat(identifier.kind()).isEqualTo(expected);
    }

    @Test
    void upperCasesToACanonicalValue() {
        var actualIdentifier = new PassportNumber("ab1234567");

        thenValueIs(actualIdentifier, "AB1234567");
    }

    private void thenValueIs(final OfficialIdentifier identifier, final String expected) {
        assertThat(identifier.value()).isEqualTo(expected);
    }

    @Test
    void rejectsAMalformedNumber() {
        var actualThrown = catchThrowable(() -> new PassportNumber("A1234567"));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
