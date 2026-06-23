package dev.blumek.party.capabilities.domain;

import java.util.Set;

public record CatalogScope(Set<String> items) implements CapabilityScope {

    public CatalogScope {
        items = Tags.normalize(items, "Catalog scope");
    }

    public boolean covers(final String item) {
        return Tags.canonical(item) && items.contains(Tags.key(item));
    }

    @Override
    public String dimension() {
        return "CATALOG";
    }

    @Override
    public boolean satisfies(final CapabilityScope requirement) {
        return requirement instanceof CatalogScope required && items.containsAll(required.items);
    }
}
