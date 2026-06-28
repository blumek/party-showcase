package dev.blumek.party.capabilities.infrastructure.persistence;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "capability_portfolio", schema = "capabilities")
record CapabilityPortfolioRecord(
        @Id UUID ownerId,
        @Version long version,
        @MappedCollection(idColumn = "owner_id") Set<CapabilityRecord> capabilities
) {
}
