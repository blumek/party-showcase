package dev.blumek.party.relationships.application;

import java.util.List;
import java.util.Optional;

import dev.blumek.party.relationships.domain.RelationshipLedger;
import dev.blumek.party.shared.OwnerId;

public interface RelationshipRepository {

    RelationshipLedger save(RelationshipLedger ledger);

    Optional<RelationshipLedger> findByOwner(OwnerId owner);

    List<RelationshipLedger> findAll();
}
