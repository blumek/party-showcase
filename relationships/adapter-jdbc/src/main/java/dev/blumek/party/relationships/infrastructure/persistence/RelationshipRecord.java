package dev.blumek.party.relationships.infrastructure.persistence;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "relationship", schema = "relationships")
record RelationshipRecord(
        @Id UUID id,
        UUID fromParty,
        String fromRole,
        UUID toParty,
        String toRole,
        String type,
        LocalDate validFrom,
        LocalDate validTo
) {
}
