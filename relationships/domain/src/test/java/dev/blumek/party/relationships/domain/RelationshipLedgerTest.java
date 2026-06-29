package dev.blumek.party.relationships.domain;

import java.time.LocalDate;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RelationshipLedgerTest {

    private final OwnerId owner = OwnerId.random();
    private final RelationshipType employment = RelationshipType.of("Employment");

    @Test
    void establishingANewRelationshipStoresIt() {
        var ledger = RelationshipLedger.openFor(owner);
        var relationship = givenRelationship();

        ledger.establish(relationship);

        thenLedgerContains(ledger, relationship);
    }

    private Relationship givenRelationship() {
        return new Relationship(RelationshipId.random(),
                Endpoint.of(owner, Role.of("Employer")),
                Endpoint.of(OwnerId.random(), Role.of("Employee")),
                employment, RelationshipPeriod.always());
    }

    private void thenLedgerContains(final RelationshipLedger ledger, final Relationship relationship) {
        assertThat(ledger.relationships()).contains(relationship);
    }

    @Test
    void establishingANewRelationshipRaisesRelationshipEstablished() {
        var ledger = RelationshipLedger.openFor(owner);
        var relationship = givenRelationship();

        ledger.establish(relationship);

        thenEventsAre(ledger, new RelationshipEstablished(owner, relationship.id(), employment));
    }

    private void thenEventsAre(final RelationshipLedger ledger, final DomainEvent... expected) {
        assertThat(ledger.domainEvents()).containsExactly(expected);
    }

    @Test
    void revisingAnExistingRelationshipRaisesRelationshipRevised() {
        var ledger = givenLedgerHolding(givenRelationship());
        var stored = ledger.relationships().getFirst();

        ledger.establish(new Relationship(stored.id(), stored.from(), stored.to(), stored.type(),
                RelationshipPeriod.from(LocalDate.of(2026, 1, 1))));

        thenEventsAre(ledger, new RelationshipRevised(owner, stored.id(), employment));
    }

    private RelationshipLedger givenLedgerHolding(final Relationship relationship) {
        var ledger = RelationshipLedger.openFor(owner);
        ledger.establish(relationship);
        ledger.clearDomainEvents();
        return ledger;
    }

    @Test
    void establishingIdenticalContentRaisesNoEvent() {
        var ledger = givenLedgerHolding(givenRelationship());
        var stored = ledger.relationships().getFirst();

        ledger.establish(new Relationship(stored.id(), stored.from(), stored.to(), stored.type(), stored.validity()));

        thenNoEventsRaised(ledger);
    }

    private void thenNoEventsRaised(final RelationshipLedger ledger) {
        assertThat(ledger.domainEvents()).isEmpty();
    }

    @Test
    void terminatingAKnownRelationshipRaisesRelationshipTerminated() {
        var ledger = givenLedgerHolding(givenRelationship());
        var stored = ledger.relationships().getFirst();

        ledger.terminate(stored.id());

        thenEventsAre(ledger, new RelationshipTerminated(owner, stored.id(), employment));
    }

    @Test
    void establishingARelationshipWithDisallowedRolesReturnsRolesNotAllowed() {
        var ledger = RelationshipLedger.openFor(owner);

        var actualResult = ledger.establish(new Relationship(RelationshipId.random(),
                Endpoint.of(owner, Role.of("Employee")),
                Endpoint.of(OwnerId.random(), Role.of("Employer")),
                employment, RelationshipPeriod.always()));

        thenFailedWith(actualResult, RelationshipError.RolesNotAllowed.class);
    }

    @Test
    void terminatingAnUnknownRelationshipReturnsRelationshipNotFound() {
        var ledger = RelationshipLedger.openFor(owner);

        var actualResult = ledger.terminate(RelationshipId.random());

        thenFailedWith(actualResult, RelationshipError.RelationshipNotFound.class);
    }

    private void thenFailedWith(final Result<RelationshipError, RelationshipId> result,
            final Class<? extends RelationshipError> expected) {
        final RelationshipError actualError = result.fold(error -> error, id -> null);
        assertThat(actualError).isInstanceOf(expected);
    }
}
