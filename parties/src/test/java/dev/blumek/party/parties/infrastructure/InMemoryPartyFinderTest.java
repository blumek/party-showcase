package dev.blumek.party.parties.infrastructure;

import java.time.LocalDate;
import java.util.List;

import dev.blumek.party.parties.application.PartyRepository;
import dev.blumek.party.parties.application.PartySearchCriteria;
import dev.blumek.party.parties.domain.Company;
import dev.blumek.party.parties.domain.LegalName;
import dev.blumek.party.parties.domain.Party;
import dev.blumek.party.parties.domain.Person;
import dev.blumek.party.parties.domain.PersonName;
import dev.blumek.party.parties.domain.PersonProfile;
import dev.blumek.party.parties.domain.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryPartyFinderTest {

    private final PartyRepository repository = mock(PartyRepository.class);
    private final InMemoryPartyFinder finder = new InMemoryPartyFinder(repository);

    @Test
    void returnsEveryPartyWhenCriteriaIsEmpty() {
        givenStored(person(), company());

        var actual = finder.search(PartySearchCriteria.any());

        assertThat(actual).hasSize(2);
    }

    @Test
    void filtersByRole() {
        givenStored(person(), company());

        var actual = finder.search(new PartySearchCriteria(null, "Customer", null, null));

        assertThat(actual).singleElement().extracting(summary -> summary.kind()).isEqualTo("PERSON");
    }

    @Test
    void filtersByType() {
        givenStored(person(), company());

        var actual = finder.search(new PartySearchCriteria("COMPANY", null, null, null));

        assertThat(actual).singleElement().extracting(summary -> summary.displayName()).isEqualTo("Acme");
    }

    @Test
    void filtersByNameFragmentIgnoringCase() {
        givenStored(person(), company());

        var actual = finder.search(new PartySearchCriteria(null, null, null, "ada"));

        assertThat(actual).singleElement().extracting(summary -> summary.displayName()).isEqualTo("Ada Lovelace");
    }

    private void givenStored(final Party... parties) {
        when(repository.findAll()).thenReturn(List.of(parties));
    }

    private Person person() {
        var person = Person.register(new PersonProfile(new PersonName("Ada", "Lovelace"), LocalDate.of(1815, 12, 10)));
        person.assignRole(Role.named("Customer"));
        return person;
    }

    private Company company() {
        return Company.register(new LegalName("Acme"));
    }
}
