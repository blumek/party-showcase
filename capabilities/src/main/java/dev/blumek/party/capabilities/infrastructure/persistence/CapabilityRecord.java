package dev.blumek.party.capabilities.infrastructure.persistence;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "capability", schema = "capabilities")
record CapabilityRecord(
        @Id UUID id,
        String kind,
        LocalDate validFrom,
        LocalDate validTo,
        @MappedCollection(idColumn = "capability_id") Set<CapabilityScopeRecord> scopes
) {
}
