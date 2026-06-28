package dev.blumek.party.parties.infrastructure.persistence;

import org.springframework.data.relational.core.mapping.Table;

@Table(name = "party_identifier", schema = "parties")
record PartyIdentifierRecord(String kind, String value) {
}
