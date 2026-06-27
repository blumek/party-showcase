package dev.blumek.party.relationships.infrastructure;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import dev.blumek.party.relationships.application.RelationshipFinder;
import dev.blumek.party.relationships.application.RelationshipQuery;
import dev.blumek.party.relationships.application.RelationshipRepository;
import dev.blumek.party.relationships.application.RelationshipSummaries;
import dev.blumek.party.relationships.application.RelationshipSummary;
import dev.blumek.party.relationships.domain.Relationship;
import dev.blumek.party.shared.OwnerId;

@Repository
@Profile("!jdbc")
class InMemoryRelationshipFinder implements RelationshipFinder {

    private final RelationshipRepository repository;

    InMemoryRelationshipFinder(final RelationshipRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<RelationshipSummary> find(final RelationshipQuery query) {
        return repository.findAll().stream()
                .flatMap(ledger -> ledger.relationships().stream())
                .filter(relationship -> matches(relationship, query))
                .map(RelationshipSummaries::of)
                .toList();
    }

    private static boolean matches(final Relationship relationship, final RelationshipQuery query) {
        return matchesDirection(relationship, query) && matchesType(relationship, query.type());
    }

    private static boolean matchesDirection(final Relationship relationship, final RelationshipQuery query) {
        final var owner = query.owner();
        return switch (query.direction()) {
            case OUTGOING -> outgoing(relationship, owner, query.role());
            case INCOMING -> incoming(relationship, owner, query.role());
            case ANY -> outgoing(relationship, owner, query.role()) || incoming(relationship, owner, query.role());
        };
    }

    private static boolean outgoing(final Relationship relationship, final OwnerId owner, final String role) {
        return relationship.from().party().equals(owner)
                && (role == null || relationship.to().role().asString().equals(role));
    }

    private static boolean incoming(final Relationship relationship, final OwnerId owner, final String role) {
        return relationship.to().party().equals(owner)
                && (role == null || relationship.from().role().asString().equals(role));
    }

    private static boolean matchesType(final Relationship relationship, final String type) {
        return type == null || relationship.type().asString().equals(type);
    }
}
