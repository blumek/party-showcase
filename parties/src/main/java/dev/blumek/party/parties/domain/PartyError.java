package dev.blumek.party.parties.domain;

public sealed interface PartyError {

    record RoleAlreadyHeld(Role role) implements PartyError {
    }

    record RoleNotHeld(Role role) implements PartyError {
    }

    record IdentifierAlreadyHeld(OfficialIdentifier identifier) implements PartyError {
    }

    record IdentifierNotHeld(OfficialIdentifier identifier) implements PartyError {
    }

    record IdentifierNotEligible(IdentifierKind kind) implements PartyError {
    }
}
