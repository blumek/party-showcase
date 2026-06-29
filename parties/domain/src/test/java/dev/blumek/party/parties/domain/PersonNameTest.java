package dev.blumek.party.parties.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PersonNameTest {

    @Test
    void trimsSurroundingWhitespace() {
        var actualName = new PersonName("  Ada  ", "  Lovelace  ");

        thenNameIs(actualName, "Ada", "Lovelace");
    }

    private void thenNameIs(final PersonName name, final String given, final String family) {
        assertThat(name.given()).isEqualTo(given);
        assertThat(name.family()).isEqualTo(family);
    }

    @Test
    void rejectsABlankGivenName() {
        var actualThrown = catchThrowable(() -> new PersonName("   ", "Lovelace"));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsABlankFamilyName() {
        var actualThrown = catchThrowable(() -> new PersonName("Ada", "   "));

        thenIllegalArgumentIsThrown(actualThrown);
    }
}
