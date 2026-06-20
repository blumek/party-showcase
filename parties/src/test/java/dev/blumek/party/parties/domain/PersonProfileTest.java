package dev.blumek.party.parties.domain;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PersonProfileTest {

    @Test
    void retainsItsNameAndDateOfBirth() {
        var name = new PersonName("Ada", "Lovelace");
        var dateOfBirth = LocalDate.of(1815, 12, 10);

        var actualProfile = new PersonProfile(name, dateOfBirth);

        thenProfileHas(actualProfile, name, dateOfBirth);
    }

    private void thenProfileHas(final PersonProfile profile, final PersonName name, final LocalDate dateOfBirth) {
        assertThat(profile.name()).isEqualTo(name);
        assertThat(profile.dateOfBirth()).isEqualTo(dateOfBirth);
    }

    @Test
    void rejectsAMissingName() {
        var actualThrown = catchThrowable(() -> new PersonProfile(null, LocalDate.of(1815, 12, 10)));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAMissingDateOfBirth() {
        var actualThrown = catchThrowable(() -> new PersonProfile(new PersonName("Ada", "Lovelace"), null));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    @Test
    void rejectsADateOfBirthInTheFuture() {
        var actualThrown = catchThrowable(
                () -> new PersonProfile(new PersonName("Ada", "Lovelace"), LocalDate.now().plusDays(1)));

        thenIllegalArgumentIsThrown(actualThrown);
    }
}
