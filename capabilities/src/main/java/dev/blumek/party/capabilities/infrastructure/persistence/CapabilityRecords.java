package dev.blumek.party.capabilities.infrastructure.persistence;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

@Profile("jdbc")
interface CapabilityRecords extends CrudRepository<CapabilityPortfolioRecord, UUID> {
}
