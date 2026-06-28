package dev.blumek.party.relationships.application;

import java.util.Optional;

import dev.blumek.party.relationships.domain.Endpoint;
import dev.blumek.party.relationships.domain.Relationship;
import dev.blumek.party.relationships.domain.RelationshipError;
import dev.blumek.party.relationships.domain.RelationshipId;
import dev.blumek.party.relationships.domain.RelationshipLedger;
import dev.blumek.party.relationships.domain.RelationshipPeriod;
import dev.blumek.party.relationships.domain.RelationshipType;
import dev.blumek.party.relationships.domain.Role;
import dev.blumek.party.shared.DomainEventPublisher;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RelationshipServiceTest {

    private final RelationshipRepository repository = mock(RelationshipRepository.class);
    private final DomainEventPublisher publisher = mock(DomainEventPublisher.class);
    private final RelationshipService service = new RelationshipService(repository, publisher);

    private final OwnerId employer = OwnerId.random();
    private final OwnerId employee = OwnerId.random();
    private final RelationshipType employment = RelationshipType.of("Employment");

    @Test
    void establishingForANewOwnerPersistsTheLedger() {
        givenNoLedgerFor(employer);

        service.establish(establishCommand());

        thenALedgerWasSaved();
    }

    private void givenNoLedgerFor(final OwnerId owner) {
        when(repository.findByOwner(owner)).thenReturn(Optional.empty());
    }

    private EstablishRelationship establishCommand() {
        return new EstablishRelationship(employer, employee, Role.of("Employer"), Role.of("Employee"),
                employment, RelationshipPeriod.always());
    }

    private void thenALedgerWasSaved() {
        verify(repository).save(any(RelationshipLedger.class));
    }

    @Test
    void establishingReturnsTheNewRelationshipId() {
        givenNoLedgerFor(employer);

        var actualResult = service.establish(establishCommand());

        thenSucceeded(actualResult);
    }

    private void thenSucceeded(final Result<RelationshipError, RelationshipId> result) {
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void establishingPublishesTheRaisedEvents() {
        givenNoLedgerFor(employer);

        service.establish(establishCommand());

        thenEventsWerePublished();
    }

    private void thenEventsWerePublished() {
        verify(publisher).publishAll(any());
    }

    @Test
    void establishingWithDisallowedRolesReturnsRolesNotAllowed() {
        givenNoLedgerFor(employer);

        var actualResult = service.establish(new EstablishRelationship(employer, employee,
                Role.of("Employee"), Role.of("Employer"), employment, RelationshipPeriod.always()));

        thenFailedWith(actualResult, RelationshipError.RolesNotAllowed.class);
    }

    private void thenFailedWith(final Result<RelationshipError, RelationshipId> result,
            final Class<? extends RelationshipError> expected) {
        final RelationshipError actualError = result.fold(error -> error, id -> null);
        assertThat(actualError).isInstanceOf(expected);
    }

    @Test
    void establishingWithDisallowedRolesPersistsNothing() {
        givenNoLedgerFor(employer);

        service.establish(new EstablishRelationship(employer, employee,
                Role.of("Employee"), Role.of("Employer"), employment, RelationshipPeriod.always()));

        thenNothingWasSaved();
    }

    private void thenNothingWasSaved() {
        verify(repository, never()).save(any());
    }

    @Test
    void terminatingForAnUnknownOwnerReturnsRelationshipNotFound() {
        givenNoLedgerFor(employer);

        var actualResult = service.terminate(new TerminateRelationship(employer, RelationshipId.random()));

        thenFailedWith(actualResult, RelationshipError.RelationshipNotFound.class);
    }

    @Test
    void terminatingAKnownRelationshipPersistsTheLedger() {
        var stored = givenStoredRelationship();
        givenLedgerHolding(stored);

        service.terminate(new TerminateRelationship(employer, stored.id()));

        thenALedgerWasSaved();
    }

    private Relationship givenStoredRelationship() {
        return new Relationship(RelationshipId.random(),
                Endpoint.of(employer, Role.of("Employer")),
                Endpoint.of(employee, Role.of("Employee")),
                employment, RelationshipPeriod.always());
    }

    private void givenLedgerHolding(final Relationship relationship) {
        var ledger = RelationshipLedger.openFor(employer);
        ledger.establish(relationship);
        ledger.clearDomainEvents();
        when(repository.findByOwner(employer)).thenReturn(Optional.of(ledger));
    }
}
