package dev.blumek.party.parties.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.blumek.party.parties.domain.PartyId;

@Service
public class PartyQueryService {

    private final PartyRepository repository;
    private final PartyFinder finder;

    public PartyQueryService(final PartyRepository repository, final PartyFinder finder) {
        this.repository = repository;
        this.finder = finder;
    }

    public Optional<PartySummary> findById(final PartyId id) {
        return repository.findById(id).map(PartySummaries::of);
    }

    public List<PartySummary> search(final PartySearchCriteria criteria) {
        return finder.search(criteria);
    }
}
