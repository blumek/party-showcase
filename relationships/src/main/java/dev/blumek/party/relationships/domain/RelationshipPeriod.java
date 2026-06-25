package dev.blumek.party.relationships.domain;

import java.time.LocalDate;

import static dev.blumek.party.shared.Guards.require;

public record RelationshipPeriod(LocalDate from, LocalDate to) {

    public RelationshipPeriod {
        if (from != null && to != null) {
            require(!to.isBefore(from), "Relationship period end cannot precede its start");
        }
    }

    public static RelationshipPeriod always() {
        return new RelationshipPeriod(null, null);
    }

    public static RelationshipPeriod from(final LocalDate start) {
        return new RelationshipPeriod(start, null);
    }

    public static RelationshipPeriod until(final LocalDate end) {
        return new RelationshipPeriod(null, end);
    }

    public static RelationshipPeriod between(final LocalDate start, final LocalDate end) {
        return new RelationshipPeriod(start, end);
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

    public boolean overlaps(final RelationshipPeriod other) {
        require(other != null, "Other relationship period cannot be null");
        return startsBefore(from, other.to) && startsBefore(other.from, to);
    }

    private static boolean startsBefore(final LocalDate start, final LocalDate end) {
        return start == null || end == null || start.isBefore(end);
    }
}
