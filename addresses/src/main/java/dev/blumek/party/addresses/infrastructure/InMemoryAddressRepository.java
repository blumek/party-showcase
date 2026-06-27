package dev.blumek.party.addresses.infrastructure;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import dev.blumek.party.addresses.application.AddressRepository;
import dev.blumek.party.addresses.domain.AddressBook;
import dev.blumek.party.shared.OwnerId;

@Repository
@Profile("!jdbc")
class InMemoryAddressRepository implements AddressRepository {

    private final Map<OwnerId, AddressBook> books = new ConcurrentHashMap<>();

    @Override
    public AddressBook save(final AddressBook book) {
        books.put(book.owner(), book);
        return book;
    }

    @Override
    public Optional<AddressBook> findByOwner(final OwnerId owner) {
        return Optional.ofNullable(books.get(owner));
    }
}
