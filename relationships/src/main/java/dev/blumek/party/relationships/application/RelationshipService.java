package dev.blumek.party.relationships.application;

import org.springframework.stereotype.Service;

import dev.blumek.party.relationships.domain.Endpoint;
import dev.blumek.party.relationships.domain.Relationship;
import dev.blumek.party.relationships.domain.RelationshipError;
import dev.blumek.party.relationships.domain.RelationshipId;
import dev.blumek.party.relationships.domain.RelationshipLedger;
import dev.blumek.party.shared.DomainEventPublisher;
import dev.blumek.party.shared.Result;

@Service
public class RelationshipService {

    private final RelationshipRepository repository;
    private final DomainEventPublisher publisher;

    public RelationshipService(final RelationshipRepository repository, final DomainEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Result<RelationshipError, RelationshipId> establish(final EstablishRelationship command) {
        final var ledger = repository.findByOwner(command.from())
                .orElseGet(() -> RelationshipLedger.openFor(command.from()));
        final var relationship = new Relationship(
                RelationshipId.random(),
                Endpoint.of(command.from(), command.fromRole()),
                Endpoint.of(command.to(), command.toRole()),
                command.type(), command.validity());
        return ledger.establish(relationship).onSuccess(id -> persist(ledger));
    }

    public Result<RelationshipError, RelationshipId> terminate(final TerminateRelationship command) {
        return repository.findByOwner(command.from())
                .map(ledger -> ledger.terminate(command.id()).onSuccess(id -> persist(ledger)))
                .orElseGet(() -> Result.failure(new RelationshipError.RelationshipNotFound(command.id())));
    }

    private void persist(final RelationshipLedger ledger) {
        repository.save(ledger);
        publisher.publishAll(ledger.domainEvents());
        ledger.clearDomainEvents();
    }
}
