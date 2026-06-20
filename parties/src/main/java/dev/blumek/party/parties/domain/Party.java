package dev.blumek.party.parties.domain;

import java.util.HashSet;
import java.util.Set;

import dev.blumek.party.shared.AggregateRoot;
import dev.blumek.party.shared.Guards;
import dev.blumek.party.shared.Result;

public abstract sealed class Party extends AggregateRoot<PartyId> permits Person, Organization {

    private final PartyId id;
    private final Set<Role> roles = new HashSet<>();
    private final Set<OfficialIdentifier> identifiers = new HashSet<>();

    protected Party(final PartyId id) {
        Guards.require(id != null, "Party requires an id");
        this.id = id;
    }

    @Override
    public PartyId id() {
        return id;
    }

    public Set<Role> roles() {
        return Set.copyOf(roles);
    }

    public Set<OfficialIdentifier> identifiers() {
        return Set.copyOf(identifiers);
    }

    protected abstract boolean accepts(IdentifierKind kind);

    public Result<PartyError, Party> assignRole(final Role role) {
        Guards.require(role != null, "Role cannot be null");
        if (roles.contains(role)) {
            return Result.failure(new PartyError.RoleAlreadyHeld(role));
        }
        roles.add(role);
        raise(new RoleAssigned(id, role));
        return Result.success(this);
    }

    public Result<PartyError, Party> relinquishRole(final Role role) {
        Guards.require(role != null, "Role cannot be null");
        if (!roles.contains(role)) {
            return Result.failure(new PartyError.RoleNotHeld(role));
        }
        roles.remove(role);
        raise(new RoleRelinquished(id, role));
        return Result.success(this);
    }

    public Result<PartyError, Party> registerIdentifier(final OfficialIdentifier identifier) {
        Guards.require(identifier != null, "Identifier cannot be null");
        if (!accepts(identifier.kind())) {
            return Result.failure(new PartyError.IdentifierNotEligible(identifier.kind()));
        }
        if (identifiers.contains(identifier)) {
            return Result.failure(new PartyError.IdentifierAlreadyHeld(identifier));
        }
        identifiers.add(identifier);
        raise(new IdentifierRegistered(id, identifier));
        return Result.success(this);
    }

    public Result<PartyError, Party> withdrawIdentifier(final OfficialIdentifier identifier) {
        Guards.require(identifier != null, "Identifier cannot be null");
        if (!identifiers.contains(identifier)) {
            return Result.failure(new PartyError.IdentifierNotHeld(identifier));
        }
        identifiers.remove(identifier);
        raise(new IdentifierWithdrawn(id, identifier));
        return Result.success(this);
    }
}
