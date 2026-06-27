package dev.blumek.party.capabilities.infrastructure.persistence;

import org.springframework.data.relational.core.mapping.Table;

@Table("capability_scope_day")
record CapabilityScopeDayRecord(String day) {
}
