package dev.blumek.party.capabilities.web;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import dev.blumek.party.capabilities.domain.VolumePeriod;

public record ScopeRequest(
        String dimension,
        Set<String> values,
        String label,
        Integer rank,
        Integer cap,
        VolumePeriod period,
        Set<DayOfWeek> days,
        LocalTime opensAt,
        LocalTime closesAt) {
}
