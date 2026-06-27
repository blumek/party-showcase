package dev.blumek.party.addresses.infrastructure.persistence;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("address")
record AddressRecord(
        @Id UUID id,
        String kind,
        String line1,
        String line2,
        String city,
        String postalCode,
        String country,
        String email,
        String phone,
        String websiteUrl,
        LocalDate validFrom,
        LocalDate validTo,
        @MappedCollection(idColumn = "address_id") Set<AddressPurposeRecord> purposes
) {
}
