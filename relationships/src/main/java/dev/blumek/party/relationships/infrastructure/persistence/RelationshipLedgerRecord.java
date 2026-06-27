package dev.blumek.party.relationships.infrastructure.persistence;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("relationship_ledger")
record RelationshipLedgerRecord(
        @Id UUID ownerId,
        long version,
        @MappedCollection(idColumn = "owner_id") Set<RelationshipRecord> relationships
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
