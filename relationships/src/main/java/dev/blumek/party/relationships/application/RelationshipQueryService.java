package dev.blumek.party.relationships.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.blumek.party.relationships.domain.Relationship;
import dev.blumek.party.relationships.domain.RelationshipId;
import dev.blumek.party.shared.OwnerId;

@Service
public class RelationshipQueryService {

    private final RelationshipRepository repository;

    public RelationshipQueryService(final RelationshipRepository repository) {
        this.repository = repository;
    }

    public List<RelationshipSummary> findByOwner(final OwnerId owner) {
        return repository.findByOwner(owner)
                .map(ledger -> ledger.relationships().stream().map(RelationshipQueryService::summarise).toList())
                .orElseGet(List::of);
    }

    public Optional<RelationshipSummary> findById(final OwnerId owner, final RelationshipId id) {
        return repository.findByOwner(owner)
                .flatMap(ledger -> ledger.find(id))
                .map(RelationshipQueryService::summarise);
    }

    public List<RelationshipSummary> findInvolving(final OwnerId party) {
        return repository.findAll().stream()
                .flatMap(ledger -> ledger.relationships().stream())
                .filter(relationship -> relationship.isActiveNow() && relationship.involves(party))
                .map(RelationshipQueryService::summarise)
                .toList();
    }

    private static RelationshipSummary summarise(final Relationship relationship) {
        return new RelationshipSummary(
                relationship.id().asString(),
                relationship.from().party().asString(),
                relationship.from().role().asString(),
                relationship.to().party().asString(),
                relationship.to().role().asString(),
                relationship.type().asString(),
                relationship.validity().from(),
                relationship.validity().to());
    }
}
