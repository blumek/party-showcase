package dev.blumek.party.addresses.infrastructure.persistence;

import org.springframework.data.relational.core.mapping.Table;

@Table(name = "address_purpose", schema = "addresses")
record AddressPurposeRecord(String purpose) {
}
