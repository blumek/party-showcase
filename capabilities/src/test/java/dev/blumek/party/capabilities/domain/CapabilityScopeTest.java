package dev.blumek.party.capabilities.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CapabilityScopeTest {

    @Test
    void aBroaderAreaSatisfiesANarrowerOne() {
        var held = new AreaScope(Set.of("PL", "DE", "CZ"));

        var actualSatisfies = held.satisfies(new AreaScope(Set.of("pl", "de")));

        thenTrue(actualSatisfies);
    }

    private void thenTrue(final boolean actual) {
        assertThat(actual).isTrue();
    }

    @Test
    void aNarrowerAreaDoesNotSatisfyABroaderOne() {
        var held = new AreaScope(Set.of("PL"));

        var actualSatisfies = held.satisfies(new AreaScope(Set.of("PL", "DE")));

        thenFalse(actualSatisfies);
    }

    private void thenFalse(final boolean actual) {
        assertThat(actual).isFalse();
    }

    @Test
    void aHigherGradeSatisfiesALowerRequirement() {
        var actualSatisfies = GradeScope.SENIOR.satisfies(GradeScope.REGULAR);

        thenTrue(actualSatisfies);
    }

    @Test
    void aLowerGradeDoesNotSatisfyAHigherRequirement() {
        var actualSatisfies = GradeScope.REGULAR.satisfies(GradeScope.MASTER);

        thenFalse(actualSatisfies);
    }

    @Test
    void aLargerVolumeOfTheSamePeriodSatisfiesASmallerRequirement() {
        var held = new VolumeScope(100, VolumePeriod.DAILY);

        var actualSatisfies = held.satisfies(new VolumeScope(50, VolumePeriod.DAILY));

        thenTrue(actualSatisfies);
    }

    @Test
    void aVolumeOfADifferentPeriodDoesNotSatisfyTheRequirement() {
        var held = new VolumeScope(100, VolumePeriod.DAILY);

        var actualSatisfies = held.satisfies(new VolumeScope(50, VolumePeriod.WEEKLY));

        thenFalse(actualSatisfies);
    }

    @Test
    void aWiderScheduleSatisfiesANarrowerRequirement() {
        var held = new ScheduleScope(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY), LocalTime.of(8, 0), LocalTime.of(18, 0));

        var actualSatisfies = held.satisfies(
                new ScheduleScope(Set.of(DayOfWeek.MONDAY), LocalTime.of(9, 0), LocalTime.of(17, 0)));

        thenTrue(actualSatisfies);
    }

    @Test
    void aScopeNeverSatisfiesARequirementOfAnotherDimension() {
        var held = new AreaScope(Set.of("PL"));

        var actualSatisfies = held.satisfies(new StandardScope(Set.of("HTTP")));

        thenFalse(actualSatisfies);
    }

    @Test
    void areaMembershipIsCaseInsensitive() {
        var scope = new AreaScope(Set.of("PL", "DE"));

        var actualCovers = scope.covers(" de ");

        thenTrue(actualCovers);
    }
}
