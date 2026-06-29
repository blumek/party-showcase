package dev.blumek.party.parties.infrastructure.persistence;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

@Profile("jdbc")
interface PartyRecords extends CrudRepository<PartyRecord, UUID> {
}
