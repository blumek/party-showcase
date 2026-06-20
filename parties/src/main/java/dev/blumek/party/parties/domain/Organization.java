package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.Guards;

public abstract sealed class Organization extends Party permits Company, OrganizationUnit {

    private LegalName name;

    protected Organization(final PartyId id, final LegalName name) {
        super(id);
        Guards.require(name != null, "Organization requires a name");
        this.name = name;
    }

    public LegalName name() {
        return name;
    }

    public void rename(final LegalName newName) {
        Guards.require(newName != null, "Organization name cannot be null");
        this.name = newName;
        raise(new OrganizationRenamed(id(), newName));
    }

    @Override
    protected boolean accepts(final IdentifierKind kind) {
        return kind == IdentifierKind.TAX;
    }
}
