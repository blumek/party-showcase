package dev.blumek.party.capabilities.infrastructure.persistence;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("capability_portfolio")
record CapabilityPortfolioRecord(
        @Id UUID ownerId,
        long version,
        @MappedCollection(idColumn = "owner_id") Set<CapabilityRecord> capabilities
) implements Persistable<UUID> {

    @Override
    public UUID getId() {
        return ownerId;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
