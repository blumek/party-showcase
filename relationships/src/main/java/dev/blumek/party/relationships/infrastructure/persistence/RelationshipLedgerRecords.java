package dev.blumek.party.relationships.infrastructure.persistence;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

@Profile("jdbc")
interface RelationshipLedgerRecords extends CrudRepository<RelationshipLedgerRecord, UUID> {
}
