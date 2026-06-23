package dev.blumek.party.capabilities.application;

import dev.blumek.party.capabilities.domain.CapabilityId;
import dev.blumek.party.shared.OwnerId;

public record RevokeCapability(OwnerId owner, CapabilityId capabilityId) {
}
