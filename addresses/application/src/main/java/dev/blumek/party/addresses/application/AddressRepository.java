package dev.blumek.party.addresses.application;

import java.util.Optional;

import dev.blumek.party.addresses.domain.AddressBook;
import dev.blumek.party.shared.OwnerId;

public interface AddressRepository {

    AddressBook save(AddressBook book);

    Optional<AddressBook> findByOwner(OwnerId owner);
}
