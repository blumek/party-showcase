package dev.blumek.party.capabilities.domain;

import static dev.blumek.party.shared.Guards.require;

public record VolumeScope(int cap, VolumePeriod period) implements CapabilityScope {

    public VolumeScope {
        require(cap >= 0, "Volume cap cannot be negative");
        require(period != null, "Volume scope requires a period");
    }

    public static VolumeScope unlimited(final VolumePeriod period) {
        return new VolumeScope(Integer.MAX_VALUE, period);
    }

    public boolean allows(final int quantity) {
        return quantity <= cap;
    }

    @Override
    public String dimension() {
        return "VOLUME";
    }

    @Override
    public boolean satisfies(final CapabilityScope requirement) {
        return requirement instanceof VolumeScope required && period == required.period && cap >= required.cap;
    }
}
