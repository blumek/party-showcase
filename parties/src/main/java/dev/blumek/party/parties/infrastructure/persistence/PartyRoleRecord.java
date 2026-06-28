package dev.blumek.party.parties.infrastructure.persistence;

import org.springframework.data.relational.core.mapping.Table;

@Table(name = "party_role", schema = "parties")
record PartyRoleRecord(String name) {
}
