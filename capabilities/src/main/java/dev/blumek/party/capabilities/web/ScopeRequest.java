package dev.blumek.party.capabilities.web;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;

import dev.blumek.party.capabilities.domain.VolumePeriod;

public record ScopeRequest(
        @NotBlank String dimension,
        Set<String> values,
        String label,
        Integer rank,
        Integer cap,
        VolumePeriod period,
        Set<DayOfWeek> days,
        LocalTime opensAt,
        LocalTime closesAt) {
}
