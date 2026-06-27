package dev.blumek.party.parties.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import dev.blumek.party.parties.application.PartyRepository;
import dev.blumek.party.parties.domain.Party;
import dev.blumek.party.parties.domain.PartyId;

@Repository
@Profile("jdbc")
class JdbcPartyRepository implements PartyRepository {

    private final PartyRecords records;
    private final PartyRecordMapper mapper;

    JdbcPartyRepository(final PartyRecords records, final PartyRecordMapper mapper) {
        this.records = records;
        this.mapper = mapper;
    }

    @Override
    public Party save(final Party party) {
        records.deleteById(party.id().value());
        records.save(mapper.toRecord(party));
        return party;
    }

    @Override
    public Optional<Party> findById(final PartyId id) {
        return records.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Party> findAll() {
        return StreamSupport.stream(records.findAll().spliterator(), false)
                .map(mapper::toDomain)
                .toList();
    }
}
