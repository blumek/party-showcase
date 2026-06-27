package dev.blumek.party.relationships.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import dev.blumek.party.relationships.application.RelationshipRepository;
import dev.blumek.party.relationships.domain.RelationshipLedger;
import dev.blumek.party.shared.OwnerId;

@Repository
@Profile("jdbc")
class JdbcRelationshipRepository implements RelationshipRepository {

    private final RelationshipLedgerRecords records;
    private final RelationshipRecordMapper mapper;

    JdbcRelationshipRepository(final RelationshipLedgerRecords records, final RelationshipRecordMapper mapper) {
        this.records = records;
        this.mapper = mapper;
    }

    @Override
    public RelationshipLedger save(final RelationshipLedger ledger) {
        records.deleteById(ledger.owner().value());
        records.save(mapper.toRecord(ledger));
        return ledger;
    }

    @Override
    public Optional<RelationshipLedger> findByOwner(final OwnerId owner) {
        return records.findById(owner.value()).map(mapper::toDomain);
    }

    @Override
    public List<RelationshipLedger> findAll() {
        return StreamSupport.stream(records.findAll().spliterator(), false)
                .map(mapper::toDomain)
                .toList();
    }
}
