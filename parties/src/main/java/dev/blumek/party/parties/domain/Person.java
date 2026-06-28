package dev.blumek.party.parties.domain;

import java.util.Set;

import dev.blumek.party.shared.Version;

import static dev.blumek.party.shared.Guards.require;

public final class Person extends Party {

    private PersonProfile profile;

    private Person(final PartyId id, final PersonProfile profile) {
        super(id);
        this.profile = profile;
    }

    public static Person register(final PersonProfile profile) {
        require(profile != null, "Person requires a profile");
        final var person = new Person(PartyId.random(), profile);
        person.raise(new PartyRegistered(person.id()));
        return person;
    }

    public static Person rehydrate(final PartyId id, final PersonProfile profile, final Version version,
                                   final Set<Role> roles, final Set<OfficialIdentifier> identifiers) {
        final var person = new Person(id, profile);
        person.restore(version, roles, identifiers);
        return person;
    }

    public PersonProfile profile() {
        return profile;
    }

    public void updateProfile(final PersonProfile updated) {
        require(updated != null, "Updated profile cannot be null");
        this.profile = updated;
        raise(new PersonProfileUpdated(id(), updated));
    }

    @Override
    protected boolean accepts(final IdentifierKind kind) {
        return kind == IdentifierKind.NATIONAL || kind == IdentifierKind.PASSPORT;
    }
}
