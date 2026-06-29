package dev.blumek.party.relationships.domain;

import dev.blumek.party.shared.OwnerId;

import static dev.blumek.party.shared.Guards.require;

public record Endpoint(OwnerId party, Role role) {

    public Endpoint {
        require(party != null, "Endpoint requires a party");
        require(role != null, "Endpoint requires a role");
    }

    public static Endpoint of(final OwnerId party, final Role role) {
        return new Endpoint(party, role);
    }
}
