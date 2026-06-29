package dev.blumek.party.capabilities.domain;

import java.util.Set;

public record AssetScope(Set<String> assets) implements CapabilityScope {

    public AssetScope {
        assets = Tags.normalize(assets, "Asset scope");
    }

    public boolean has(final String asset) {
        return Tags.canonical(asset) && assets.contains(Tags.key(asset));
    }

    @Override
    public String dimension() {
        return "ASSET";
    }

    @Override
    public boolean satisfies(final CapabilityScope requirement) {
        return requirement instanceof AssetScope required && assets.containsAll(required.assets);
    }
}
