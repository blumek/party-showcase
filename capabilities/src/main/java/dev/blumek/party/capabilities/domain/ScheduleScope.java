package dev.blumek.party.capabilities.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static dev.blumek.party.shared.Guards.require;

public record ScheduleScope(Set<DayOfWeek> days, LocalTime opensAt, LocalTime closesAt) implements CapabilityScope {

    public ScheduleScope {
        require(days != null && !days.isEmpty(), "Schedule scope requires at least one day");
        require(opensAt != null && closesAt != null, "Schedule scope requires opening and closing times");
        require(opensAt.isBefore(closesAt), "Schedule opening time must precede its closing time");
        days = Set.copyOf(days);
    }

    public boolean available(final DayOfWeek day, final LocalTime time) {
        require(day != null && time != null, "Day and time cannot be null");
        return days.contains(day) && !time.isBefore(opensAt) && time.isBefore(closesAt);
    }

    @Override
    public String dimension() {
        return "SCHEDULE";
    }

    @Override
    public boolean satisfies(final CapabilityScope requirement) {
        return requirement instanceof ScheduleScope required
                && days.containsAll(required.days)
                && !opensAt.isAfter(required.opensAt)
                && !closesAt.isBefore(required.closesAt);
    }
}
