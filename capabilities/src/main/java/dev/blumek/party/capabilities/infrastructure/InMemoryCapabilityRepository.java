package dev.blumek.party.capabilities.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import dev.blumek.party.capabilities.application.CapabilityRepository;
import dev.blumek.party.capabilities.domain.CapabilityPortfolio;
import dev.blumek.party.shared.OwnerId;

@Repository
@Profile("!jdbc")
class InMemoryCapabilityRepository implements CapabilityRepository {

    private final Map<OwnerId, CapabilityPortfolio> portfolios = new ConcurrentHashMap<>();

    @Override
    public CapabilityPortfolio save(final CapabilityPortfolio portfolio) {
        portfolios.put(portfolio.owner(), portfolio);
        return portfolio;
    }

    @Override
    public Optional<CapabilityPortfolio> findByOwner(final OwnerId owner) {
        return Optional.ofNullable(portfolios.get(owner));
    }

    @Override
    public List<CapabilityPortfolio> findAll() {
        return List.copyOf(portfolios.values());
    }
}
