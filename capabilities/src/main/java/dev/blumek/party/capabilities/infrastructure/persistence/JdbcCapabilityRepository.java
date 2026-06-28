package dev.blumek.party.capabilities.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import dev.blumek.party.capabilities.application.CapabilityRepository;
import dev.blumek.party.capabilities.domain.CapabilityPortfolio;
import dev.blumek.party.shared.OwnerId;

@Repository
@Profile("jdbc")
class JdbcCapabilityRepository implements CapabilityRepository {

    private final CapabilityRecords records;
    private final CapabilityRecordMapper mapper;

    JdbcCapabilityRepository(final CapabilityRecords records, final CapabilityRecordMapper mapper) {
        this.records = records;
        this.mapper = mapper;
    }

    @Override
    public CapabilityPortfolio save(final CapabilityPortfolio portfolio) {
        records.save(mapper.toRecord(portfolio));
        return portfolio;
    }

    @Override
    public Optional<CapabilityPortfolio> findByOwner(final OwnerId owner) {
        return records.findById(owner.value()).map(mapper::toDomain);
    }

    @Override
    public List<CapabilityPortfolio> findAll() {
        return StreamSupport.stream(records.findAll().spliterator(), false)
                .map(mapper::toDomain)
                .toList();
    }
}
