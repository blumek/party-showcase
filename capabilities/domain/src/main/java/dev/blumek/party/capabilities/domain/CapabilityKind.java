package dev.blumek.party.capabilities.domain;

import static dev.blumek.party.shared.Guards.requireText;

public record CapabilityKind(String value) {

    public CapabilityKind {
        value = requireText(value, "Capability kind cannot be blank").strip();
    }

    public static CapabilityKind of(final String value) {
        return new CapabilityKind(value);
    }

    public String asString() {
        return value;
    }
}
