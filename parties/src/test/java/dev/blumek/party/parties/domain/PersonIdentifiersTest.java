package dev.blumek.party.parties.domain;

import java.time.LocalDate;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.Result;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonIdentifiersTest {

    @Test
    void registeringAnIdentifierAddsItToTheParty() {
        var person = givenPerson();

        person.registerIdentifier(givenIdentifier());

        thenIdentifiersContain(person, givenIdentifier());
    }

    private Person givenPerson() {
        var person = Person.register(new PersonProfile(new PersonName("Ada", "Lovelace"), LocalDate.of(1815, 12, 10)));
        person.clearDomainEvents();
        return person;
    }

    private OfficialIdentifier givenIdentifier() {
        return new NationalIdentificationNumber("44051401458");
    }

    private void thenIdentifiersContain(final Person person, final OfficialIdentifier identifier) {
        assertThat(person.identifiers()).contains(identifier);
    }

    @Test
    void registeringAnIdentifierRaisesIdentifierRegistered() {
        var person = givenPerson();

        person.registerIdentifier(givenIdentifier());

        thenEventsAre(person, new IdentifierRegistered(person.id(), givenIdentifier()));
    }

    private void thenEventsAre(final Person person, final DomainEvent... expected) {
        assertThat(person.domainEvents()).containsExactly(expected);
    }

    @Test
    void registeringAnIdentifierAlreadyHeldReturnsIdentifierAlreadyHeld() {
        var person = givenPersonHolding(givenIdentifier());

        var actualResult = person.registerIdentifier(givenIdentifier());

        thenFailedWith(actualResult, PartyError.IdentifierAlreadyHeld.class);
    }

    private Person givenPersonHolding(final OfficialIdentifier identifier) {
        var person = givenPerson();
        person.registerIdentifier(identifier);
        person.clearDomainEvents();
        return person;
    }

    private void thenFailedWith(final Result<PartyError, Party> result, final Class<? extends PartyError> expected) {
        final PartyError actualError = result.fold(error -> error, party -> null);
        assertThat(actualError).isInstanceOf(expected);
    }

    @Test
    void withdrawingAHeldIdentifierRemovesIt() {
        var person = givenPersonHolding(givenIdentifier());

        person.withdrawIdentifier(givenIdentifier());

        thenIdentifiersAreEmpty(person);
    }

    private void thenIdentifiersAreEmpty(final Person person) {
        assertThat(person.identifiers()).isEmpty();
    }

    @Test
    void withdrawingAnIdentifierNotHeldReturnsIdentifierNotHeld() {
        var person = givenPerson();

        var actualResult = person.withdrawIdentifier(givenIdentifier());

        thenFailedWith(actualResult, PartyError.IdentifierNotHeld.class);
    }
}
