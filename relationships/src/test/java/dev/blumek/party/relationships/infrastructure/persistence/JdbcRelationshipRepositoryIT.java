package dev.blumek.party.relationships.infrastructure.persistence;

import dev.blumek.party.relationships.domain.Endpoint;
import dev.blumek.party.relationships.domain.Relationship;
import dev.blumek.party.relationships.domain.RelationshipId;
import dev.blumek.party.relationships.domain.RelationshipLedger;
import dev.blumek.party.relationships.domain.RelationshipPeriod;
import dev.blumek.party.relationships.domain.RelationshipType;
import dev.blumek.party.relationships.domain.Role;
import dev.blumek.party.shared.OwnerId;
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
@Import({JdbcRelationshipRepository.class, RelationshipRecordMapper.class})
class JdbcRelationshipRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private JdbcRelationshipRepository repository;

    private final OwnerId employer = OwnerId.random();
    private final OwnerId employee = OwnerId.random();

    @Test
    void roundTripsARelationshipEdge() {
        var ledger = givenEmploymentLedger();

        var actual = savedAndReloaded(ledger);

        var relationship = actual.relationships().getFirst();
        assertThat(relationship.type()).isEqualTo(RelationshipType.of("Employment"));
        assertThat(relationship.from()).isEqualTo(Endpoint.of(employer, Role.of("Employer")));
        assertThat(relationship.to()).isEqualTo(Endpoint.of(employee, Role.of("Employee")));
    }

    @Test
    void preservesTheAggregateVersion() {
        var ledger = givenEmploymentLedger();

        var actual = savedAndReloaded(ledger);

        assertThat(actual.version()).isEqualTo(ledger.version());
    }

    private RelationshipLedger givenEmploymentLedger() {
        var ledger = RelationshipLedger.openFor(employer);
        ledger.establish(new Relationship(RelationshipId.random(),
                Endpoint.of(employer, Role.of("Employer")),
                Endpoint.of(employee, Role.of("Employee")),
                RelationshipType.of("Employment"), RelationshipPeriod.always()));
        return ledger;
    }

    private RelationshipLedger savedAndReloaded(final RelationshipLedger ledger) {
        repository.save(ledger);
        return repository.findByOwner(ledger.owner()).orElseThrow();
    }
}
