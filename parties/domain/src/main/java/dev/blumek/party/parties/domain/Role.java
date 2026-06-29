package dev.blumek.party.parties.domain;

import static dev.blumek.party.shared.Guards.require;

public record Role(RoleName name) {

    public Role {
        require(name != null, "Role requires a name");
    }

    public static Role named(final String name) {
        return new Role(new RoleName(name));
    }
}
