package dev.blumek.party.parties.domain;

import java.util.Set;

import dev.blumek.party.shared.Version;

public final class OrganizationUnit extends Organization {

    private OrganizationUnit(final PartyId id, final LegalName name) {
        super(id, name);
    }

    public static OrganizationUnit register(final LegalName name) {
        final var unit = new OrganizationUnit(PartyId.random(), name);
        unit.raise(new PartyRegistered(unit.id()));
        return unit;
    }

    public static OrganizationUnit rehydrate(final PartyId id, final LegalName name, final Version version,
                                             final Set<Role> roles, final Set<OfficialIdentifier> identifiers) {
        final var unit = new OrganizationUnit(id, name);
        unit.restore(version, roles, identifiers);
        return unit;
    }
}
