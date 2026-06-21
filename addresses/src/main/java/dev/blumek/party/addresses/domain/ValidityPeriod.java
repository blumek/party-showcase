package dev.blumek.party.addresses.domain;

import java.time.LocalDate;

import dev.blumek.party.shared.Guards;

public record ValidityPeriod(LocalDate from, LocalDate to) {

    public ValidityPeriod {
        if (from != null && to != null) {
            Guards.require(!to.isBefore(from), "Validity end cannot precede its start");
        }
    }

    public static ValidityPeriod always() {
        return new ValidityPeriod(null, null);
    }

    public static ValidityPeriod from(final LocalDate start) {
        return new ValidityPeriod(start, null);
    }

    public static ValidityPeriod until(final LocalDate end) {
        return new ValidityPeriod(null, end);
    }

    public static ValidityPeriod between(final LocalDate start, final LocalDate end) {
        return new ValidityPeriod(start, end);
    }

    public boolean contains(final LocalDate date) {
        Guards.require(date != null, "Date cannot be null");
        final var afterStart = from == null || !date.isBefore(from);
        final var beforeEnd = to == null || date.isBefore(to);
        return afterStart && beforeEnd;
    }

    public boolean isActiveNow() {
        return contains(LocalDate.now());
    }

    public boolean overlaps(final ValidityPeriod other) {
        Guards.require(other != null, "Other validity period cannot be null");
        return startsBefore(from, other.to) && startsBefore(other.from, to);
    }

    private static boolean startsBefore(final LocalDate start, final LocalDate end) {
        return start == null || end == null || start.isBefore(end);
    }
}
