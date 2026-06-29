package dev.blumek.party.parties.domain;

import java.time.LocalDate;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.Result;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonRolesTest {

    @Test
    void assigningARoleAddsItToTheParty() {
        var person = givenPerson();

        person.assignRole(Role.named("Customer"));

        thenRolesContain(person, Role.named("Customer"));
    }

    private Person givenPerson() {
        var person = Person.register(new PersonProfile(new PersonName("Ada", "Lovelace"), LocalDate.of(1815, 12, 10)));
        person.clearDomainEvents();
        return person;
    }

    private void thenRolesContain(final Person person, final Role role) {
        assertThat(person.roles()).contains(role);
    }

    @Test
    void assigningARoleRaisesRoleAssigned() {
        var person = givenPerson();

        person.assignRole(Role.named("Customer"));

        thenEventsAre(person, new RoleAssigned(person.id(), Role.named("Customer")));
    }

    private void thenEventsAre(final Person person, final DomainEvent... expected) {
        assertThat(person.domainEvents()).containsExactly(expected);
    }

    @Test
    void assigningARoleAlreadyHeldReturnsRoleAlreadyHeld() {
        var person = givenPersonHolding(Role.named("Customer"));

        var actualResult = person.assignRole(Role.named("Customer"));

        thenFailedWith(actualResult, PartyError.RoleAlreadyHeld.class);
    }

    private Person givenPersonHolding(final Role role) {
        var person = givenPerson();
        person.assignRole(role);
        person.clearDomainEvents();
        return person;
    }

    private void thenFailedWith(final Result<PartyError, Party> result, final Class<? extends PartyError> expected) {
        final PartyError actualError = result.fold(error -> error, party -> null);
        assertThat(actualError).isInstanceOf(expected);
    }

    @Test
    void relinquishingAHeldRoleRemovesIt() {
        var person = givenPersonHolding(Role.named("Customer"));

        person.relinquishRole(Role.named("Customer"));

        thenRolesAreEmpty(person);
    }

    private void thenRolesAreEmpty(final Person person) {
        assertThat(person.roles()).isEmpty();
    }

    @Test
    void relinquishingARoleNotHeldReturnsRoleNotHeld() {
        var person = givenPerson();

        var actualResult = person.relinquishRole(Role.named("Customer"));

        thenFailedWith(actualResult, PartyError.RoleNotHeld.class);
    }
}
