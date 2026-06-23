package dev.blumek.party.capabilities.domain;

import java.util.Set;

public record StandardScope(Set<String> standards) implements CapabilityScope {

    public StandardScope {
        standards = Tags.normalize(standards, "Standard scope");
    }

    public boolean supports(final String standard) {
        return Tags.canonical(standard) && standards.contains(Tags.key(standard));
    }

    @Override
    public String dimension() {
        return "STANDARD";
    }

    @Override
    public boolean satisfies(final CapabilityScope requirement) {
        return requirement instanceof StandardScope required && standards.containsAll(required.standards);
    }
}
