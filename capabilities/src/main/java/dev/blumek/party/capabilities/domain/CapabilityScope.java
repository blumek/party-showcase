package dev.blumek.party.capabilities.domain;

public sealed interface CapabilityScope
        permits AreaScope, GradeScope, VolumeScope, ScheduleScope, StandardScope, CatalogScope, AssetScope {

    String dimension();

    boolean satisfies(CapabilityScope requirement);
}
