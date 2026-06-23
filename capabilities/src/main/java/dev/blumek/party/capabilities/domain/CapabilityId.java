package dev.blumek.party.capabilities.domain;

import java.util.UUID;

import static dev.blumek.party.shared.Guards.require;

public record CapabilityId(UUID value) {

    public CapabilityId {
        require(value != null, "Capability id value cannot be null");
    }

    public static CapabilityId of(final String value) {
        return new CapabilityId(UUID.fromString(value));
    }

    public static CapabilityId random() {
        return new CapabilityId(UUID.randomUUID());
    }

    public String asString() {
        return value.toString();
    }
}
