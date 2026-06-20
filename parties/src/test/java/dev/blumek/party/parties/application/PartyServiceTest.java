package dev.blumek.party.parties.application;

import java.time.LocalDate;
import java.util.Optional;

import dev.blumek.party.parties.domain.Company;
import dev.blumek.party.parties.domain.LegalName;
import dev.blumek.party.parties.domain.Party;
import dev.blumek.party.parties.domain.PartyError;
import dev.blumek.party.parties.domain.PartyId;
import dev.blumek.party.parties.domain.Person;
import dev.blumek.party.parties.domain.PersonName;
import dev.blumek.party.parties.domain.PersonProfile;
import dev.blumek.party.parties.domain.Role;
import dev.blumek.party.shared.DomainEventPublisher;
import dev.blumek.party.shared.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PartyServiceTest {

    private final PartyStore store = mock(PartyStore.class);
    private final DomainEventPublisher publisher = mock(DomainEventPublisher.class);
    private final PartyService service = new PartyService(store, publisher);

    @Test
    void registeringAPersonPersistsTheNewPartyAndReturnsItsId() {
        var actualResult = service.register(new RegisterPerson(profile()));

        thenReturnedIdMatchesTheSavedParty(actualResult);
    }

    private PersonProfile profile() {
        return new PersonProfile(new PersonName("Ada", "Lovelace"), LocalDate.of(1815, 12, 10));
    }

    private void thenReturnedIdMatchesTheSavedParty(final Result<PartyError, PartyId> result) {
        final var captor = ArgumentCaptor.forClass(Party.class);
        verify(store).save(captor.capture());
        final PartyId returnedId = result.fold(error -> null, id -> id);
        assertThat(returnedId).isEqualTo(captor.getValue().id());
    }

    @Test
    void registeringAPersonPublishesItsEvents() {
        service.register(new RegisterPerson(profile()));

        thenEventsWerePublished();
    }

    private void thenEventsWerePublished() {
        verify(publisher).publishAll(any());
    }

    @Test
    void assigningARoleToAKnownPartyReturnsItsId() {
        var person = givenStored(registeredPerson());

        var actualResult = service.assignRole(new AssignRole(person.id(), Role.named("Customer")));

        thenReturnedId(actualResult, person.id());
    }

    private Person registeredPerson() {
        var person = Person.register(profile());
        person.clearDomainEvents();
        return person;
    }

    private <T extends Party> T givenStored(final T party) {
        when(store.findById(party.id())).thenReturn(Optional.of(party));
        return party;
    }

    private void thenReturnedId(final Result<PartyError, PartyId> result, final PartyId expected) {
        final PartyId returnedId = result.fold(error -> null, id -> id);
        assertThat(returnedId).isEqualTo(expected);
    }

    @Test
    void assigningARoleToAnUnknownPartyReturnsPartyNotFound() {
        givenNoPartyIsStored();

        var actualResult = service.assignRole(new AssignRole(PartyId.random(), Role.named("Customer")));

        thenFailedWith(actualResult, PartyError.PartyNotFound.class);
    }

    private void givenNoPartyIsStored() {
        when(store.findById(any())).thenReturn(Optional.empty());
    }

    private void thenFailedWith(final Result<PartyError, PartyId> result, final Class<? extends PartyError> expected) {
        final PartyError actualError = result.fold(error -> error, id -> null);
        assertThat(actualError).isInstanceOf(expected);
    }

    @Test
    void aRejectedRoleChangeIsNotPersisted() {
        var person = givenStored(personHoldingCustomerRole());

        service.assignRole(new AssignRole(person.id(), Role.named("Customer")));

        thenNothingWasSaved();
    }

    private Person personHoldingCustomerRole() {
        var person = registeredPerson();
        person.assignRole(Role.named("Customer"));
        person.clearDomainEvents();
        return person;
    }

    private void thenNothingWasSaved() {
        verify(store, never()).save(any());
    }

    @Test
    void aRejectedRoleChangeIsNotPublished() {
        var person = givenStored(personHoldingCustomerRole());

        service.assignRole(new AssignRole(person.id(), Role.named("Customer")));

        thenNothingWasPublished();
    }

    private void thenNothingWasPublished() {
        verify(publisher, never()).publishAll(any());
    }

    @Test
    void renamingAKnownOrganizationReturnsItsId() {
        var company = givenStored(registeredCompany());

        var actualResult = service.rename(new RenameOrganization(company.id(), new LegalName("Acme Worldwide")));

        thenReturnedId(actualResult, company.id());
    }

    private Company registeredCompany() {
        var company = Company.register(new LegalName("Acme Industries"));
        company.clearDomainEvents();
        return company;
    }

    @Test
    void renamingANonOrganizationReturnsPartyNotFound() {
        var person = givenStored(registeredPerson());

        var actualResult = service.rename(new RenameOrganization(person.id(), new LegalName("Acme Worldwide")));

        thenFailedWith(actualResult, PartyError.PartyNotFound.class);
    }
}
