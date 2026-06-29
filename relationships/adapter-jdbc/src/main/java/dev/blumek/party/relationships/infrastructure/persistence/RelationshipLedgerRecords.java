package dev.blumek.party.relationships.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

@Profile("jdbc")
interface RelationshipLedgerRecords extends CrudRepository<RelationshipLedgerRecord, UUID> {

    @Query("select owner_id from relationships.relationship where id = :id")
    Optional<UUID> findOwnerByRelationshipId(@Param("id") UUID id);
}
