package dev.blumek.party.addresses.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PostalCodeTest {

    @Test
    void trimsAndUpperCasesToACanonicalValue() {
        var actualCode = new PostalCode("  ec1a 1bb ");

        thenValueIs(actualCode, "EC1A 1BB");
    }

    private void thenValueIs(final PostalCode code, final String expected) {
        assertThat(code.value()).isEqualTo(expected);
    }

    @Test
    void rejectsABlankValue() {
        var actualThrown = catchThrowable(() -> new PostalCode("   "));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
