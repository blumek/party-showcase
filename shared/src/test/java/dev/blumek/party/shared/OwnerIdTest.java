package dev.blumek.party.shared;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class OwnerIdTest {

    @Test
    void parsesItsValueFromAString() {
        var uuid = UUID.randomUUID();

        var actualOwnerId = OwnerId.of(uuid.toString());

        thenValueIs(actualOwnerId, uuid);
    }

    private void thenValueIs(final OwnerId ownerId, final UUID expected) {
        assertThat(ownerId.value()).isEqualTo(expected);
    }

    @Test
    void roundTripsThroughItsStringForm() {
        var ownerId = OwnerId.random();

        var actualOwnerId = OwnerId.of(ownerId.asString());

        thenOwnerIdEquals(actualOwnerId, ownerId);
    }

    private void thenOwnerIdEquals(final OwnerId ownerId, final OwnerId expected) {
        assertThat(ownerId).isEqualTo(expected);
    }

    @Test
    void rejectsANullValue() {
        var actualThrown = catchThrowable(() -> new OwnerId(null));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
