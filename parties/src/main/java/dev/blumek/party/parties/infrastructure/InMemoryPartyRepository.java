package dev.blumek.party.parties.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import dev.blumek.party.parties.application.PartyRepository;
import dev.blumek.party.parties.domain.Party;
import dev.blumek.party.parties.domain.PartyId;

@Repository
@Profile("!jdbc")
class InMemoryPartyRepository implements PartyRepository {

    private final Map<PartyId, Party> parties = new ConcurrentHashMap<>();

    @Override
    public Party save(final Party party) {
        parties.put(party.id(), party);
        return party;
    }

    @Override
    public Optional<Party> findById(final PartyId id) {
        return Optional.ofNullable(parties.get(id));
    }

    @Override
    public List<Party> findAll() {
        return List.copyOf(parties.values());
    }
}
