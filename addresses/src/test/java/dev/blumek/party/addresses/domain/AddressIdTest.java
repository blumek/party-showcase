package dev.blumek.party.addresses.domain;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class AddressIdTest {

    @Test
    void parsesAStringIntoTheSameId() {
        var raw = UUID.randomUUID().toString();

        var actualId = AddressId.of(raw);

        thenStringValueIs(actualId, raw);
    }

    private void thenStringValueIs(final AddressId id, final String expected) {
        assertThat(id.asString()).isEqualTo(expected);
    }

    @Test
    void mintsADistinctRandomIdEachTime() {
        var actualId = AddressId.random();

        thenDiffersFrom(actualId, AddressId.random());
    }

    private void thenDiffersFrom(final AddressId id, final AddressId other) {
        assertThat(id).isNotEqualTo(other);
    }

    @Test
    void rejectsANullValue() {
        var actualThrown = catchThrowable(() -> new AddressId(null));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
