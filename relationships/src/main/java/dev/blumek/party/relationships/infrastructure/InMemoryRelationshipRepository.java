package dev.blumek.party.relationships.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import dev.blumek.party.relationships.application.RelationshipRepository;
import dev.blumek.party.relationships.domain.RelationshipLedger;
import dev.blumek.party.shared.OwnerId;

@Repository
@Profile("!jdbc")
class InMemoryRelationshipRepository implements RelationshipRepository {

    private final Map<OwnerId, RelationshipLedger> ledgers = new ConcurrentHashMap<>();

    @Override
    public RelationshipLedger save(final RelationshipLedger ledger) {
        ledgers.put(ledger.owner(), ledger);
        return ledger;
    }

    @Override
    public Optional<RelationshipLedger> findByOwner(final OwnerId owner) {
        return Optional.ofNullable(ledgers.get(owner));
    }

    @Override
    public List<RelationshipLedger> findAll() {
        return List.copyOf(ledgers.values());
    }
}
