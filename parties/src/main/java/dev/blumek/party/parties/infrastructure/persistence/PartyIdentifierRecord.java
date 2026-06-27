package dev.blumek.party.parties.infrastructure.persistence;

import org.springframework.data.relational.core.mapping.Table;

@Table("party_identifier")
record PartyIdentifierRecord(String kind, String value) {
}
