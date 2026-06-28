package dev.blumek.party.relationships.infrastructure.persistence;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "relationship_ledger", schema = "relationships")
record RelationshipLedgerRecord(
        @Id UUID ownerId,
        @Version long version,
        @MappedCollection(idColumn = "owner_id") Set<RelationshipRecord> relationships
) {
}
