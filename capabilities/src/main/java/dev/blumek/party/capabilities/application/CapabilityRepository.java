package dev.blumek.party.capabilities.application;

import java.util.List;
import java.util.Optional;

import dev.blumek.party.capabilities.domain.CapabilityPortfolio;
import dev.blumek.party.shared.OwnerId;

public interface CapabilityRepository {

    CapabilityPortfolio save(CapabilityPortfolio portfolio);

    Optional<CapabilityPortfolio> findByOwner(OwnerId owner);

    List<CapabilityPortfolio> findAll();
}
