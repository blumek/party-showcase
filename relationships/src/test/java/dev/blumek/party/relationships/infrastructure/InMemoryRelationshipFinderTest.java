package dev.blumek.party.relationships.infrastructure;

import java.util.List;

import dev.blumek.party.relationships.application.RelationshipQuery;
import dev.blumek.party.relationships.application.RelationshipQuery.Direction;
import dev.blumek.party.relationships.application.RelationshipSummary;
import dev.blumek.party.relationships.domain.Endpoint;
import dev.blumek.party.relationships.domain.Relationship;
import dev.blumek.party.relationships.domain.RelationshipId;
import dev.blumek.party.relationships.domain.RelationshipLedger;
import dev.blumek.party.relationships.domain.RelationshipPeriod;
import dev.blumek.party.relationships.domain.RelationshipType;
import dev.blumek.party.relationships.domain.Role;
import dev.blumek.party.shared.OwnerId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryRelationshipFinderTest {

    private final InMemoryRelationshipRepository repository = new InMemoryRelationshipRepository();
    private final InMemoryRelationshipFinder finder = new InMemoryRelationshipFinder(repository);

    private final OwnerId employer = OwnerId.random();
    private final OwnerId employee = OwnerId.random();
    private final OwnerId club = OwnerId.random();

    @Test
    void findsOutgoingEmployeesOfAnEmployer() {
        givenEmployment(employer, employee);
        givenMembership(club, employer);

        var found = finder.find(new RelationshipQuery(employer, Direction.OUTGOING, "Employment", "Employee"));

        assertThat(found).extracting(RelationshipSummary::toParty).containsExactly(employee.asString());
    }

    @Test
    void findsIncomingRelationshipsByCounterpartyRole() {
        givenEmployment(employer, employee);

        var found = finder.find(new RelationshipQuery(employee, Direction.INCOMING, null, "Employer"));

        assertThat(found).extracting(RelationshipSummary::fromParty).containsExactly(employer.asString());
    }

    @Test
    void anyDirectionReturnsRelationshipsOnEitherEnd() {
        givenEmployment(employer, employee);
        givenMembership(club, employee);

        var found = finder.find(new RelationshipQuery(employee, Direction.ANY, null, null));

        assertThat(found).hasSize(2);
    }

    @Test
    void filtersByTypeAcrossDirections() {
        givenEmployment(employer, employee);
        givenMembership(club, employee);

        var found = finder.find(new RelationshipQuery(employee, Direction.ANY, "Membership", null));

        assertThat(found).extracting(RelationshipSummary::type).containsExactly("Membership");
    }

    private void givenEmployment(final OwnerId from, final OwnerId to) {
        store(from, relationship(from, "Employer", to, "Employee", "Employment"));
    }

    private void givenMembership(final OwnerId from, final OwnerId to) {
        store(from, relationship(from, "Organization", to, "Member", "Membership"));
    }

    private void store(final OwnerId owner, final Relationship relationship) {
        var ledger = repository.findByOwner(owner).orElseGet(() -> RelationshipLedger.openFor(owner));
        ledger.establish(relationship);
        repository.save(ledger);
    }

    private static Relationship relationship(final OwnerId from, final String fromRole, final OwnerId to,
            final String toRole, final String type) {
        return new Relationship(RelationshipId.random(),
                Endpoint.of(from, Role.of(fromRole)),
                Endpoint.of(to, Role.of(toRole)),
                RelationshipType.of(type), RelationshipPeriod.always());
    }
}
