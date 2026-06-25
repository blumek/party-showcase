package dev.blumek.party.relationships.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class RelationshipTypeTest {

    @Test
    void trimsSurroundingWhitespace() {
        var actualType = RelationshipType.of("  Employment  ");

        thenValueIs(actualType, "Employment");
    }

    private void thenValueIs(final RelationshipType type, final String expected) {
        assertThat(type.asString()).isEqualTo(expected);
    }

    @Test
    void rejectsABlankValue() {
        var actualThrown = catchThrowable(() -> RelationshipType.of("   "));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
