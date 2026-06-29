package dev.blumek.party.relationships.infrastructure.persistence;

import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import dev.blumek.party.relationships.domain.Endpoint;
import dev.blumek.party.relationships.domain.Relationship;
import dev.blumek.party.relationships.domain.RelationshipId;
import dev.blumek.party.relationships.domain.RelationshipLedger;
import dev.blumek.party.relationships.domain.RelationshipPeriod;
import dev.blumek.party.relationships.domain.RelationshipType;
import dev.blumek.party.relationships.domain.Role;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Version;

@Component
@Profile("jdbc")
class RelationshipRecordMapper {

    RelationshipLedgerRecord toRecord(final RelationshipLedger ledger) {
        final var relationships = ledger.relationships().stream()
                .map(RelationshipRecordMapper::relationshipRecord)
                .collect(Collectors.toSet());
        return new RelationshipLedgerRecord(ledger.owner().value(), ledger.version().number(), relationships);
    }

    RelationshipLedger toDomain(final RelationshipLedgerRecord entity) {
        final var relationships = entity.relationships().stream()
                .map(RelationshipRecordMapper::relationship)
                .toList();
        return RelationshipLedger.rehydrate(new OwnerId(entity.ownerId()), relationships, new Version(entity.version()));
    }

    private static RelationshipRecord relationshipRecord(final Relationship relationship) {
        return new RelationshipRecord(relationship.id().value(),
                relationship.from().party().value(), relationship.from().role().asString(),
                relationship.to().party().value(), relationship.to().role().asString(),
                relationship.type().asString(),
                relationship.validity().from(), relationship.validity().to());
    }

    private static Relationship relationship(final RelationshipRecord entity) {
        return new Relationship(new RelationshipId(entity.id()),
                Endpoint.of(new OwnerId(entity.fromParty()), Role.of(entity.fromRole())),
                Endpoint.of(new OwnerId(entity.toParty()), Role.of(entity.toRole())),
                RelationshipType.of(entity.type()),
                new RelationshipPeriod(entity.validFrom(), entity.validTo()));
    }
}
