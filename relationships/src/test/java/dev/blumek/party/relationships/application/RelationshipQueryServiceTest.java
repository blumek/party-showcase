package dev.blumek.party.relationships.application;

import java.time.LocalDate;
import java.util.List;
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
        var stored = givenStored(employment(RelationshipPeriod.from(LocalDate.of(2026, 1, 1))));

        var actualSummary = service.findById(employer, stored.id()).orElseThrow();

        thenSummaryDescribes(actualSummary);
    }

    private Relationship employment(final RelationshipPeriod validity) {
        return new Relationship(RelationshipId.random(),
                Endpoint.of(employer, Role.of("Employer")),
                Endpoint.of(employee, Role.of("Employee")), employment, validity);
    }

    private Relationship givenStored(final Relationship relationship) {
        var ledger = RelationshipLedger.openFor(employer);
        ledger.establish(relationship);
        when(repository.findByOwner(employer)).thenReturn(Optional.of(ledger));
        return relationship;
    }

    private void thenSummaryDescribes(final RelationshipSummary summary) {
        assertThat(summary.fromRole()).isEqualTo("Employer");
        assertThat(summary.toRole()).isEqualTo("Employee");
        assertThat(summary.type()).isEqualTo("Employment");
        assertThat(summary.validFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    }

    @Test
    void returnsEmptyForAnUnknownRelationship() {
        givenStored(employment(RelationshipPeriod.always()));

        var actualSummary = service.findById(employer, RelationshipId.random());

        thenAbsent(actualSummary);
    }

    private void thenAbsent(final Optional<RelationshipSummary> summary) {
        assertThat(summary).isEmpty();
    }

    @Test
    void returnsAnEmptyListForAnUnknownOwner() {
        when(repository.findByOwner(employer)).thenReturn(Optional.empty());

        var actualSummaries = service.findByOwner(employer);

        thenEmpty(actualSummaries);
    }

    private void thenEmpty(final List<RelationshipSummary> summaries) {
        assertThat(summaries).isEmpty();
    }

    @Test
    void findsRelationshipsWherePartyIsTheToEndpoint() {
        givenAllLedgers(ledgerFor(employer, employment(RelationshipPeriod.always())));

        var actualSummaries = service.findInvolving(employee);

        thenCountIs(actualSummaries, 1);
    }

    private void givenAllLedgers(final RelationshipLedger... ledgers) {
        when(repository.findAll()).thenReturn(List.of(ledgers));
    }

    private RelationshipLedger ledgerFor(final OwnerId owner, final Relationship relationship) {
        var ledger = RelationshipLedger.openFor(owner);
        ledger.establish(relationship);
        return ledger;
    }

    private void thenCountIs(final List<RelationshipSummary> summaries, final int expected) {
        assertThat(summaries).hasSize(expected);
    }

    @Test
    void excludesExpiredRelationshipsFromInvolving() {
        givenAllLedgers(ledgerFor(employer, employment(RelationshipPeriod.until(LocalDate.of(2020, 1, 1)))));

        var actualSummaries = service.findInvolving(employee);

        thenCountIs(actualSummaries, 0);
    }
}
