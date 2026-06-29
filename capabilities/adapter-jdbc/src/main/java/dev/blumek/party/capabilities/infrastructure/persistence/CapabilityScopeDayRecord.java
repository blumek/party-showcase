package dev.blumek.party.capabilities.infrastructure.persistence;

import org.springframework.data.relational.core.mapping.Table;

@Table(name = "capability_scope_day", schema = "capabilities")
record CapabilityScopeDayRecord(String day) {
}
