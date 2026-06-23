package dev.blumek.party.capabilities.domain;

import java.util.Set;

public record AreaScope(Set<String> areas) implements CapabilityScope {

    public AreaScope {
        areas = Tags.normalize(areas, "Area scope");
    }

    public boolean covers(final String area) {
        return Tags.canonical(area) && areas.contains(Tags.key(area));
    }

    @Override
    public String dimension() {
        return "AREA";
    }

    @Override
    public boolean satisfies(final CapabilityScope requirement) {
        return requirement instanceof AreaScope required && areas.containsAll(required.areas);
    }
}
