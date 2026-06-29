package dev.blumek.party.parties.domain;

import static dev.blumek.party.shared.Guards.require;

public abstract sealed class Organization extends Party permits Company, OrganizationUnit {

    private LegalName name;

    protected Organization(final PartyId id, final LegalName name) {
        super(id);
        require(name != null, "Organization requires a name");
        this.name = name;
    }

    public LegalName name() {
        return name;
    }

    public void rename(final LegalName newName) {
        require(newName != null, "Organization name cannot be null");
        this.name = newName;
        raise(new OrganizationRenamed(id(), newName));
    }

    @Override
    protected boolean accepts(final IdentifierKind kind) {
        return kind == IdentifierKind.TAX;
    }
}
