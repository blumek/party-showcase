package dev.blumek.party.addresses.infrastructure.persistence;

import org.springframework.data.relational.core.mapping.Table;

@Table("address_purpose")
record AddressPurposeRecord(String purpose) {
}
