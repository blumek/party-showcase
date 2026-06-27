package dev.blumek.party.parties.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import dev.blumek.party.parties.domain.Company;
import dev.blumek.party.parties.domain.LegalName;
import dev.blumek.party.parties.domain.NationalIdentificationNumber;
import dev.blumek.party.parties.domain.Person;
import dev.blumek.party.parties.domain.PersonName;
import dev.blumek.party.parties.domain.PersonProfile;
import dev.blumek.party.parties.domain.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PartyQueryServiceTest {

    private final PartyRepository store = mock(PartyRepository.class);
    private final PartyFinder finder = mock(PartyFinder.class);
    private final PartyQueryService service = new PartyQueryService(store, finder);

    @Test
    void summarisesAPersonWithItsKindAndDisplayName() {
        var person = givenStoredPerson();

        var actualSummary = service.findById(person.id()).orElseThrow();

        thenHeaderIs(actualSummary, "PERSON", "Ada Lovelace");
    }

    private Person givenStoredPerson() {
        var person = Person.register(new PersonProfile(new PersonName("Ada", "Lovelace"), LocalDate.of(1815, 12, 10)));
        person.assignRole(Role.named("Customer"));
        person.registerIdentifier(new NationalIdentificationNumber("44051401458"));
        person.clearDomainEvents();
        when(store.findById(person.id())).thenReturn(Optional.of(person));
        return person;
    }

    private void thenHeaderIs(final PartySummary summary, final String kind, final String displayName) {
        assertThat(summary.kind()).isEqualTo(kind);
        assertThat(summary.displayName()).isEqualTo(displayName);
    }

    @Test
    void includesThePartysRolesAndIdentifiers() {
        var person = givenStoredPerson();

        var actualSummary = service.findById(person.id()).orElseThrow();

        thenRolesAndIdentifiersAre(actualSummary);
    }

    private void thenRolesAndIdentifiersAre(final PartySummary summary) {
        assertThat(summary.roles()).containsExactly("Customer");
        assertThat(summary.identifiers())
                .containsExactly(new PartySummary.IdentifierSummary("NATIONAL", "44051401458"));
    }

    @Test
    void summarisesACompanyWithItsLegalName() {
        var company = givenStoredCompany();

        var actualSummary = service.findById(company.id()).orElseThrow();

        thenHeaderIs(actualSummary, "COMPANY", "Acme Industries");
    }

    private Company givenStoredCompany() {
        var company = Company.register(new LegalName("Acme Industries"));
        company.clearDomainEvents();
        when(store.findById(company.id())).thenReturn(Optional.of(company));
        return company;
    }

    @Test
    void returnsEmptyForAnUnknownParty() {
        var person = Person.register(new PersonProfile(new PersonName("Ada", "Lovelace"), LocalDate.of(1815, 12, 10)));
        when(store.findById(person.id())).thenReturn(Optional.empty());

        var actualSummary = service.findById(person.id());

        thenAbsent(actualSummary);
    }

    private void thenAbsent(final Optional<PartySummary> summary) {
        assertThat(summary).isEmpty();
    }

    @Test
    void delegatesSearchToTheFinder() {
        var criteria = new PartySearchCriteria("PERSON", null, null, null);
        givenFinderReturns(criteria, PartySummaries.of(givenStoredPerson()));

        var actualSummaries = service.search(criteria);

        thenSummaryCountIs(actualSummaries.size(), 1);
    }

    private void givenFinderReturns(final PartySearchCriteria criteria, final PartySummary... summaries) {
        when(finder.search(criteria)).thenReturn(List.of(summaries));
    }

    private void thenSummaryCountIs(final int actual, final int expected) {
        assertThat(actual).isEqualTo(expected);
    }
}
