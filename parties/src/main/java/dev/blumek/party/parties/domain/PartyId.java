package dev.blumek.party.parties.domain;

import java.util.UUID;

import dev.blumek.party.shared.Guards;

public record PartyId(UUID value) {

    public PartyId {
        Guards.require(value != null, "Party id value cannot be null");
    }

    public static PartyId of(final String value) {
        return new PartyId(UUID.fromString(value));
    }

    public static PartyId random() {
        return new PartyId(UUID.randomUUID());
    }

    public String asString() {
        return value.toString();
    }
}
