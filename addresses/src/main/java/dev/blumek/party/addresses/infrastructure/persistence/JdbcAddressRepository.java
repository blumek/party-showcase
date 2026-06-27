package dev.blumek.party.addresses.infrastructure.persistence;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import dev.blumek.party.addresses.application.AddressRepository;
import dev.blumek.party.addresses.domain.AddressBook;
import dev.blumek.party.shared.OwnerId;

@Repository
@Profile("jdbc")
class JdbcAddressRepository implements AddressRepository {

    private final AddressBookRecords records;
    private final AddressRecordMapper mapper;

    JdbcAddressRepository(final AddressBookRecords records, final AddressRecordMapper mapper) {
        this.records = records;
        this.mapper = mapper;
    }

    @Override
    public AddressBook save(final AddressBook book) {
        records.deleteById(book.owner().value());
        records.save(mapper.toRecord(book));
        return book;
    }

    @Override
    public Optional<AddressBook> findByOwner(final OwnerId owner) {
        return records.findById(owner.value()).map(mapper::toDomain);
    }
}
