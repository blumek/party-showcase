package dev.blumek.party.addresses.infrastructure.persistence;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("address_book")
record AddressBookRecord(
        @Id UUID ownerId,
        @Version long version,
        @MappedCollection(idColumn = "owner_id") Set<AddressRecord> addresses
) {
}
