package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.Guards;

public final class Person extends Party {

    private PersonProfile profile;

    private Person(final PartyId id, final PersonProfile profile) {
        super(id);
        this.profile = profile;
    }

    public static Person register(final PersonProfile profile) {
        Guards.require(profile != null, "Person requires a profile");
        final var person = new Person(PartyId.random(), profile);
        person.raise(new PartyRegistered(person.id()));
        return person;
    }

    public PersonProfile profile() {
        return profile;
    }

    public void updateProfile(final PersonProfile updated) {
        Guards.require(updated != null, "Updated profile cannot be null");
        this.profile = updated;
        raise(new PersonProfileUpdated(id(), updated));
    }
}
