package dev.blumek.party.relationships.application;

import java.time.LocalDate;
import java.util.Optional;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RelationshipQueryServiceTest {

    private final RelationshipRepository repository = mock(RelationshipRepository.class);
    private final RelationshipQueryService service = new RelationshipQueryService(repository);

    private final OwnerId employer = OwnerId.random();
    private final OwnerId employee = OwnerId.random();
    private final RelationshipType employment = RelationshipType.of("Employment");

    @Test
    void summarisesARelationshipWithItsEndpointsAndType() {
        var stored = givenStored(employment());

        var actualSummary = service.findById(employer, stored.id()).orElseThrow();

        thenSummaryDescribes(actualSummary);
    }

    private Relationship employment() {
        return new Relationship(RelationshipId.random(),
                Endpoint.of(employer, Role.of("Employer")),
                Endpoint.of(employee, Role.of("Employee")), employment,
                RelationshipPeriod.from(LocalDate.of(2026, 1, 1)));
    }

    private Relationship givenStored(final Relationship relationship) {
        var ledger = RelationshipLedger.openFor(employer);
        ledger.establish(relationship);
        when(repository.findContaining(relationship.id())).thenReturn(Optional.of(ledger));
        return relationship;
    }

    private void thenSummaryDescribes(final RelationshipSummary summary) {
        assertThat(summary.fromRole()).isEqualTo("Employer");
        assertThat(summary.toRole()).isEqualTo("Employee");
        assertThat(summary.type()).isEqualTo("Employment");
        assertThat(summary.validFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    }

    @Test
    void resolvesARelationshipForTheCounterpartyAtTheToEndpoint() {
        var stored = givenStored(employment());

        var actualSummary = service.findById(employee, stored.id());

        assertThat(actualSummary).isPresent();
    }

    @Test
    void returnsEmptyForAPartyThatDoesNotParticipate() {
        var stored = givenStored(employment());

        var actualSummary = service.findById(OwnerId.random(), stored.id());

        assertThat(actualSummary).isEmpty();
    }

    @Test
    void returnsEmptyForAnUnknownRelationship() {
        var actualSummary = service.findById(employer, RelationshipId.random());

        assertThat(actualSummary).isEmpty();
    }
}
