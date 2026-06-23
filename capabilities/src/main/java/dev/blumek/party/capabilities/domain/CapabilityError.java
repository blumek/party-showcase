package dev.blumek.party.capabilities.domain;

public sealed interface CapabilityError {

    record CapabilityNotFound(CapabilityId id) implements CapabilityError {
    }
}
