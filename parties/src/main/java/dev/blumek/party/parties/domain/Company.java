package dev.blumek.party.parties.domain;

import java.util.Set;

public final class Company extends Organization {

    private Company(final PartyId id, final LegalName name) {
        super(id, name);
    }

    public static Company register(final LegalName name) {
        final var company = new Company(PartyId.random(), name);
        company.raise(new PartyRegistered(company.id()));
        return company;
    }

    public static Company rehydrate(final PartyId id, final LegalName name,
                                    final Set<Role> roles, final Set<OfficialIdentifier> identifiers) {
        final var company = new Company(id, name);
        company.restore(roles, identifiers);
        return company;
    }
}
