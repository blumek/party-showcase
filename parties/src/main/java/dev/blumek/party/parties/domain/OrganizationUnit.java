package dev.blumek.party.parties.domain;

public final class OrganizationUnit extends Organization {

    private OrganizationUnit(final PartyId id, final LegalName name) {
        super(id, name);
    }

    public static OrganizationUnit register(final LegalName name) {
        final var unit = new OrganizationUnit(PartyId.random(), name);
        unit.raise(new PartyRegistered(unit.id()));
        return unit;
    }
}
