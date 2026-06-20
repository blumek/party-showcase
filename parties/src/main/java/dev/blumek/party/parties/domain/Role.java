package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.Guards;

public record Role(RoleName name) {

    public Role {
        Guards.require(name != null, "Role requires a name");
    }

    public static Role named(final String name) {
        return new Role(new RoleName(name));
    }
}
