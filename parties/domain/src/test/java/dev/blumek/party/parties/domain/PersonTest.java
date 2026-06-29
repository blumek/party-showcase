package dev.blumek.party.parties.domain;

import java.time.LocalDate;

import dev.blumek.party.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonTest {

    @Test
    void registersWithTheGivenProfile() {
        var profile = givenProfile();

        var actualPerson = Person.register(profile);

        thenProfileIs(actualPerson, profile);
    }

    private PersonProfile givenProfile() {
        return new PersonProfile(new PersonName("Ada", "Lovelace"), LocalDate.of(1815, 12, 10));
    }

    private void thenProfileIs(final Person person, final PersonProfile expected) {
        assertThat(person.profile()).isEqualTo(expected);
    }

    @Test
    void registrationRaisesPartyRegistered() {
        var actualPerson = Person.register(givenProfile());

        thenEventsAre(actualPerson, new PartyRegistered(actualPerson.id()));
    }

    private void thenEventsAre(final Person person, final DomainEvent... expected) {
        assertThat(person.domainEvents()).containsExactly(expected);
    }

    @Test
    void updatingTheProfileReplacesIt() {
        var person = givenRegisteredPerson();
        var updated = givenOtherProfile();

        person.updateProfile(updated);

        thenProfileIs(person, updated);
    }

    private Person givenRegisteredPerson() {
        var person = Person.register(givenProfile());
        person.clearDomainEvents();
        return person;
    }

    private PersonProfile givenOtherProfile() {
        return new PersonProfile(new PersonName("Grace", "Hopper"), LocalDate.of(1906, 12, 9));
    }

    @Test
    void updatingTheProfileRaisesPersonProfileUpdated() {
        var person = givenRegisteredPerson();
        var updated = givenOtherProfile();

        person.updateProfile(updated);

        thenEventsAre(person, new PersonProfileUpdated(person.id(), updated));
    }
}
