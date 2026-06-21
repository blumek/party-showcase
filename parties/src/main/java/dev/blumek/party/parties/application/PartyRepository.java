package dev.blumek.party.parties.application;

import java.util.List;
import java.util.Optional;

import dev.blumek.party.parties.domain.Party;
import dev.blumek.party.parties.domain.PartyId;

public interface PartyRepository {

    Party save(Party party);

    Optional<Party> findById(PartyId id);

    List<Party> findAll();
}
