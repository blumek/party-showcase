package dev.blumek.party.capabilities.infrastructure.persistence;

import org.springframework.data.relational.core.mapping.Table;

@Table("capability_scope_value")
record CapabilityScopeValueRecord(String value) {
}
