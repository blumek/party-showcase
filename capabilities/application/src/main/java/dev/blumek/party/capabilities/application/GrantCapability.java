package dev.blumek.party.capabilities.application;

import java.util.Set;

import dev.blumek.party.capabilities.domain.CapabilityId;
import dev.blumek.party.capabilities.domain.CapabilityKind;
import dev.blumek.party.capabilities.domain.CapabilityScope;
import dev.blumek.party.capabilities.domain.EffectivePeriod;
import dev.blumek.party.shared.OwnerId;

public record GrantCapability(
        OwnerId owner,
        CapabilityId capabilityId,
        CapabilityKind kind,
        Set<CapabilityScope> scopes,
        EffectivePeriod validity) {
}
