package dev.blumek.party.parties.infrastructure;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import dev.blumek.party.parties.application.PartyFinder;
import dev.blumek.party.parties.application.PartyRepository;
import dev.blumek.party.parties.application.PartySearchCriteria;
import dev.blumek.party.parties.application.PartySummaries;
import dev.blumek.party.parties.application.PartySummary;

import static java.util.Locale.ROOT;

@Repository
@Profile("!jdbc")
class InMemoryPartyFinder implements PartyFinder {

    private final PartyRepository repository;

    InMemoryPartyFinder(final PartyRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PartySummary> search(final PartySearchCriteria criteria) {
        return repository.findAll().stream()
                .map(PartySummaries::of)
                .filter(summary -> matches(summary, criteria))
                .toList();
    }

    private static boolean matches(final PartySummary summary, final PartySearchCriteria criteria) {
        return matchesType(summary, criteria.type())
                && matchesRole(summary, criteria.role())
                && matchesIdentifier(summary, criteria.identifier())
                && matchesName(summary, criteria.nameContains());
    }

    private static boolean matchesType(final PartySummary summary, final String type) {
        return type == null || summary.kind().equals(type);
    }

    private static boolean matchesRole(final PartySummary summary, final String role) {
        return role == null || summary.roles().contains(role);
    }

    private static boolean matchesIdentifier(final PartySummary summary, final String identifier) {
        return identifier == null || summary.identifiers().stream()
                .anyMatch(value -> value.value().equals(identifier));
    }

    private static boolean matchesName(final PartySummary summary, final String nameContains) {
        return nameContains == null
                || summary.displayName().toLowerCase(ROOT).contains(nameContains.toLowerCase(ROOT));
    }
}
