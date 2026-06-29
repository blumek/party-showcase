package dev.blumek.party.capabilities.domain;

import java.util.Optional;
import java.util.Set;

import static dev.blumek.party.shared.Guards.require;

public record Capability(CapabilityId id, CapabilityKind kind, Set<CapabilityScope> scopes, EffectivePeriod validity) {

    public Capability {
        require(id != null, "Capability requires an id");
        require(kind != null, "Capability requires a kind");
        require(scopes != null, "Capability requires scopes");
        require(validity != null, "Capability requires a validity period");
        scopes = Set.copyOf(scopes);
    }

    public boolean isActiveNow() {
        return validity.isActiveNow();
    }

    public Optional<CapabilityScope> scopeOf(final String dimension) {
        return scopes.stream().filter(scope -> scope.dimension().equals(dimension)).findFirst();
    }

    public boolean satisfies(final CapabilityNeed need) {
        return need.satisfiedBy(this);
    }

    public boolean differsFrom(final Capability other) {
        return !kind.equals(other.kind) || !scopes.equals(other.scopes) || !validity.equals(other.validity);
    }
}
