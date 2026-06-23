package dev.blumek.party.capabilities.domain;

import java.time.LocalDate;

import static dev.blumek.party.shared.Guards.require;

public record EffectivePeriod(LocalDate from, LocalDate to) {

    public EffectivePeriod {
        if (from != null && to != null) {
            require(!to.isBefore(from), "Effective period end cannot precede its start");
        }
    }

    public static EffectivePeriod always() {
        return new EffectivePeriod(null, null);
    }

    public static EffectivePeriod from(final LocalDate start) {
        return new EffectivePeriod(start, null);
    }

    public static EffectivePeriod until(final LocalDate end) {
        return new EffectivePeriod(null, end);
    }

    public static EffectivePeriod between(final LocalDate start, final LocalDate end) {
        return new EffectivePeriod(start, end);
    }

    public boolean contains(final LocalDate date) {
        require(date != null, "Date cannot be null");
        final var afterStart = from == null || !date.isBefore(from);
        final var beforeEnd = to == null || date.isBefore(to);
        return afterStart && beforeEnd;
    }

    public boolean isActiveNow() {
        return contains(LocalDate.now());
    }

    public boolean overlaps(final EffectivePeriod other) {
        require(other != null, "Other effective period cannot be null");
        return startsBefore(from, other.to) && startsBefore(other.from, to);
    }

    private static boolean startsBefore(final LocalDate start, final LocalDate end) {
        return start == null || end == null || start.isBefore(end);
    }
}
