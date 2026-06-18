package dev.blumek.party.shared;

import java.util.UUID;

public record OwnerId(UUID value) {

    public OwnerId {
        if (value == null) {
            throw new IllegalArgumentException("Owner id value cannot be null");
        }
    }

    public static OwnerId of(final String value) {
        return new OwnerId(UUID.fromString(value));
    }

    public static OwnerId random() {
        return new OwnerId(UUID.randomUUID());
    }

    public String asString() {
        return value.toString();
    }
}
