package dev.blumek.party.relationships.domain;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class RelationshipPeriodTest {

    @Test
    void anAlwaysPeriodContainsAnyDate() {
        var period = RelationshipPeriod.always();

        var actualContains = period.contains(LocalDate.of(2026, 6, 27));

        thenTrue(actualContains);
    }

    private void thenTrue(final boolean actual) {
        assertThat(actual).isTrue();
    }

    @Test
    void excludesTheEndDateBecauseTheRangeIsHalfOpen() {
        var period = RelationshipPeriod.between(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 7, 1));

        var actualContains = period.contains(LocalDate.of(2026, 7, 1));

        thenFalse(actualContains);
    }

    private void thenFalse(final boolean actual) {
        assertThat(actual).isFalse();
    }

    @Test
    void includesTheStartDate() {
        var period = RelationshipPeriod.from(LocalDate.of(2026, 1, 1));

        var actualContains = period.contains(LocalDate.of(2026, 1, 1));

        thenTrue(actualContains);
    }

    @Test
    void overlappingRangesAreDetected() {
        var period = RelationshipPeriod.between(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 7, 1));

        var actualOverlaps = period.overlaps(RelationshipPeriod.between(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 8, 1)));

        thenTrue(actualOverlaps);
    }

    @Test
    void adjacentHalfOpenRangesDoNotOverlap() {
        var period = RelationshipPeriod.between(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 7, 1));

        var actualOverlaps = period.overlaps(RelationshipPeriod.between(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 1)));

        thenFalse(actualOverlaps);
    }

    @Test
    void rejectsAnEndBeforeItsStart() {
        var actualThrown = catchThrowable(
                () -> RelationshipPeriod.between(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 1, 1)));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
