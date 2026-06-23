package dev.blumek.party.capabilities.domain;

import java.util.Set;

import static dev.blumek.party.shared.Guards.require;

public record CapabilityNeed(CapabilityKind kind, Set<CapabilityScope> required) {

    public CapabilityNeed {
        require(kind != null, "Capability need requires a kind");
        require(required != null, "Capability need requires scopes");
        required = Set.copyOf(required);
    }

    public static CapabilityNeed of(final CapabilityKind kind) {
        return new CapabilityNeed(kind, Set.of());
    }

    public boolean satisfiedBy(final Capability capability) {
        return capability.kind().equals(kind)
                && required.stream().allMatch(requirement -> matched(capability, requirement));
    }

    private static boolean matched(final Capability capability, final CapabilityScope requirement) {
        return capability.scopeOf(requirement.dimension())
                .map(scope -> scope.satisfies(requirement))
                .orElse(false);
    }
}
