package dev.blumek.party.capabilities.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CapabilityKindTest {

    @Test
    void trimsSurroundingWhitespace() {
        var actualKind = CapabilityKind.of("  MedicalImaging  ");

        thenValueIs(actualKind, "MedicalImaging");
    }

    private void thenValueIs(final CapabilityKind kind, final String expected) {
        assertThat(kind.asString()).isEqualTo(expected);
    }

    @Test
    void rejectsABlankValue() {
        var actualThrown = catchThrowable(() -> CapabilityKind.of("   "));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
