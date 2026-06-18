package dev.blumek.party.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class VersionTest {

    @Test
    void initialVersionStartsAtZero() {
        var actualVersion = Version.initial();

        thenNumberIs(actualVersion, 0);
    }

    private void thenNumberIs(final Version version, final long expected) {
        assertThat(version.number()).isEqualTo(expected);
    }

    @Test
    void nextAdvancesToTheFollowingNumber() {
        var actualVersion = Version.initial().next().next();

        thenNumberIs(actualVersion, 2);
    }

    @Test
    void aNegativeNumberIsRejected() {
        var actualThrown = catchThrowable(() -> new Version(-1));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void versionsWithTheSameNumberAreEqual() {
        var actualVersion = new Version(3);

        thenVersionEquals(actualVersion, new Version(3));
    }

    private void thenVersionEquals(final Version version, final Version expected) {
        assertThat(version).isEqualTo(expected);
    }
}
