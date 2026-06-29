package dev.blumek.party.capabilities.application;


import dev.blumek.party.capabilities.domain.Capability;
import dev.blumek.party.capabilities.domain.CapabilityError;
import dev.blumek.party.capabilities.domain.CapabilityId;
import dev.blumek.party.capabilities.domain.CapabilityPortfolio;
import dev.blumek.party.shared.DomainEventPublisher;
import dev.blumek.party.shared.Result;

public class CapabilityService {

    private final CapabilityRepository repository;
    private final DomainEventPublisher publisher;

    public CapabilityService(final CapabilityRepository repository, final DomainEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Result<CapabilityError, CapabilityId> grant(final GrantCapability command) {
        final var portfolio = repository.findByOwner(command.owner())
                .orElseGet(() -> CapabilityPortfolio.openFor(command.owner()));
        final var capability = new Capability(
                command.capabilityId() == null ? CapabilityId.random() : command.capabilityId(),
                command.kind(), command.scopes(), command.validity());
        return portfolio.grant(capability).onSuccess(id -> persist(portfolio));
    }

    public Result<CapabilityError, CapabilityId> revoke(final RevokeCapability command) {
        return repository.findByOwner(command.owner())
                .map(portfolio -> portfolio.revoke(command.capabilityId()).onSuccess(id -> persist(portfolio)))
                .orElseGet(() -> Result.failure(new CapabilityError.CapabilityNotFound(command.capabilityId())));
    }

    private void persist(final CapabilityPortfolio portfolio) {
        repository.save(portfolio);
        publisher.publishAll(portfolio.domainEvents());
        portfolio.clearDomainEvents();
    }
}
