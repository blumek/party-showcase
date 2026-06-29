package dev.blumek.party.addresses.infrastructure.persistence;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

@Profile("jdbc")
interface AddressBookRecords extends CrudRepository<AddressBookRecord, UUID> {
}
