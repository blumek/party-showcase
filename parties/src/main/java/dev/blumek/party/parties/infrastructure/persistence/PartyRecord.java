package dev.blumek.party.parties.infrastructure.persistence;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("party")
record PartyRecord(
        @Id UUID id,
        String type,
        String givenName,
        String familyName,
        LocalDate dateOfBirth,
        String legalName,
        @MappedCollection(idColumn = "party_id") Set<PartyRoleRecord> roles,
        @MappedCollection(idColumn = "party_id") Set<PartyIdentifierRecord> identifiers
) implements Persistable<UUID> {

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
