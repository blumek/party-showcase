package dev.blumek.party.relationships.application;

import java.util.Optional;


import dev.blumek.party.relationships.domain.RelationshipId;
import dev.blumek.party.shared.OwnerId;

public class RelationshipQueryService {

    private final RelationshipRepository repository;

    public RelationshipQueryService(final RelationshipRepository repository) {
        this.repository = repository;
    }

    public Optional<RelationshipSummary> findById(final OwnerId owner, final RelationshipId id) {
        return repository.findContaining(id)
                .flatMap(ledger -> ledger.find(id))
                .filter(relationship -> relationship.involves(owner))
                .map(RelationshipSummaries::of);
    }
}
