package dev.blumek.party.addresses.infrastructure.persistence;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("address_book")
record AddressBookRecord(
        @Id UUID ownerId,
        long version,
        @MappedCollection(idColumn = "owner_id") Set<AddressRecord> addresses
) implements Persistable<UUID> {

    @Override
    public UUID getId() {
        return ownerId;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
