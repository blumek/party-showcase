package dev.blumek.party.parties.domain;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PartyIdTest {

    @Test
    void parsesItsValueFromAString() {
        var uuid = UUID.randomUUID();

        var actualPartyId = PartyId.of(uuid.toString());

        thenValueIs(actualPartyId, uuid);
    }

    private void thenValueIs(final PartyId partyId, final UUID expected) {
        assertThat(partyId.value()).isEqualTo(expected);
    }

    @Test
    void roundTripsThroughItsStringForm() {
        var partyId = PartyId.random();

        var actualPartyId = PartyId.of(partyId.asString());

        thenPartyIdEquals(actualPartyId, partyId);
    }

    private void thenPartyIdEquals(final PartyId partyId, final PartyId expected) {
        assertThat(partyId).isEqualTo(expected);
    }

    @Test
    void rejectsANullValue() {
        var actualThrown = catchThrowable(() -> new PartyId(null));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
