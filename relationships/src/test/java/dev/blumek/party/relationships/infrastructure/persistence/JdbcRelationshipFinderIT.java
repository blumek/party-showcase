package dev.blumek.party.relationships.infrastructure.persistence;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = NONE)
@Testcontainers
@ActiveProfiles("jdbc")
@TestPropertySource(properties = "spring.flyway.locations=classpath:db/migration/relationship")
@Import({JdbcRelationshipRepository.class, RelationshipRecordMapper.class, JdbcRelationshipFinder.class})
class JdbcRelationshipFinderIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private JdbcRelationshipRepository repository;

    @Autowired
    private JdbcRelationshipFinder finder;

    private final OwnerId employer = OwnerId.random();
    private final OwnerId firstEmployee = OwnerId.random();
    private final OwnerId secondEmployee = OwnerId.random();
    private final OwnerId club = OwnerId.random();

    @BeforeEach
    void seed() {
        store(employer, employment(employer, firstEmployee));
        store(employer, employment(employer, secondEmployee));
        store(club, membership(club, employer));
    }

    @Test
    void findsEveryEmployeeOfAnEmployer() {
        var found = finder.find(new RelationshipQuery(employer, Direction.OUTGOING, "Employment", "Employee"));

        assertThat(found).extracting(RelationshipSummary::toParty)
                .containsExactlyInAnyOrder(firstEmployee.asString(), secondEmployee.asString());
    }

    @Test
    void findsWhoEmploysAParty() {
        var found = finder.find(new RelationshipQuery(firstEmployee, Direction.INCOMING, "Employment", "Employer"));

        assertThat(found).extracting(RelationshipSummary::fromParty).containsExactly(employer.asString());
    }

    @Test
    void anyDirectionSpansBothEnds() {
        var found = finder.find(new RelationshipQuery(employer, Direction.ANY, null, null));

        assertThat(found).hasSize(3);
    }

    @Test
    void outgoingWithoutFiltersReproducesTheOwnerLedger() {
        var found = finder.find(new RelationshipQuery(employer, Direction.OUTGOING, null, null));

        assertThat(found).hasSize(2);
    }

    private void store(final OwnerId owner, final Relationship relationship) {
        var ledger = repository.findByOwner(owner).orElseGet(() -> RelationshipLedger.openFor(owner));
        ledger.establish(relationship);
        repository.save(ledger);
    }

    private static Relationship employment(final OwnerId from, final OwnerId to) {
        return relationship(from, "Employer", to, "Employee", "Employment");
    }

    private static Relationship membership(final OwnerId from, final OwnerId to) {
        return relationship(from, "Organization", to, "Member", "Membership");
    }

    private static Relationship relationship(final OwnerId from, final String fromRole, final OwnerId to,
            final String toRole, final String type) {
        return new Relationship(RelationshipId.random(),
                Endpoint.of(from, Role.of(fromRole)),
                Endpoint.of(to, Role.of(toRole)),
                RelationshipType.of(type), RelationshipPeriod.always());
    }
}
