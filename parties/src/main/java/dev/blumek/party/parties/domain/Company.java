package dev.blumek.party.parties.domain;

public final class Company extends Organization {

    private Company(final PartyId id, final LegalName name) {
        super(id, name);
    }

    public static Company register(final LegalName name) {
        final var company = new Company(PartyId.random(), name);
        company.raise(new PartyRegistered(company.id()));
        return company;
    }
}
