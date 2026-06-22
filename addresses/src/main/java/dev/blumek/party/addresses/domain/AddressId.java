package dev.blumek.party.addresses.domain;

import java.util.UUID;

import static dev.blumek.party.shared.Guards.require;

public record AddressId(UUID value) {

    public AddressId {
        require(value != null, "Address id value cannot be null");
    }

    public static AddressId of(final String value) {
        return new AddressId(UUID.fromString(value));
    }

    public static AddressId random() {
        return new AddressId(UUID.randomUUID());
    }

    public String asString() {
        return value.toString();
    }
}
