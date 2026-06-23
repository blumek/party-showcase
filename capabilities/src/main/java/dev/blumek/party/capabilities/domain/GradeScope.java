package dev.blumek.party.capabilities.domain;

import static dev.blumek.party.shared.Guards.require;
import static dev.blumek.party.shared.Guards.requireText;
import static java.util.Locale.ROOT;

public record GradeScope(String label, int rank) implements CapabilityScope {

    public static final GradeScope TRAINEE = new GradeScope("TRAINEE", 1);
    public static final GradeScope REGULAR = new GradeScope("REGULAR", 2);
    public static final GradeScope SENIOR = new GradeScope("SENIOR", 3);
    public static final GradeScope MASTER = new GradeScope("MASTER", 4);

    public GradeScope {
        label = requireText(label, "Grade scope requires a label").strip().toUpperCase(ROOT);
        require(rank > 0, "Grade rank must be positive");
    }

    public boolean atLeast(final GradeScope other) {
        require(other != null, "Other grade cannot be null");
        return rank >= other.rank;
    }

    @Override
    public String dimension() {
        return "GRADE";
    }

    @Override
    public boolean satisfies(final CapabilityScope requirement) {
        return requirement instanceof GradeScope required && rank >= required.rank;
    }
}
