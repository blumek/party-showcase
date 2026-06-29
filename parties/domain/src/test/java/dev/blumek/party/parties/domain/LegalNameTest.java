package dev.blumek.party.parties.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class LegalNameTest {

    @Test
    void trimsSurroundingWhitespace() {
        var actualName = new LegalName("  Acme Industries  ");

        thenValueIs(actualName, "Acme Industries");
    }

    private void thenValueIs(final LegalName name, final String expected) {
        assertThat(name.value()).isEqualTo(expected);
    }

    @Test
    void rejectsBlankText() {
        var actualThrown = catchThrowable(() -> new LegalName("   "));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
