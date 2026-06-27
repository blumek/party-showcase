package dev.blumek.party.capabilities.infrastructure.persistence;

import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("capability_scope")
record CapabilityScopeRecord(
        @Id UUID id,
        String dimension,
        String gradeLabel,
        Integer gradeRank,
        Integer volumeCap,
        String volumePeriod,
        LocalTime opensAt,
        LocalTime closesAt,
        @MappedCollection(idColumn = "scope_id") Set<CapabilityScopeValueRecord> values,
        @MappedCollection(idColumn = "scope_id") Set<CapabilityScopeDayRecord> days
) {
}
