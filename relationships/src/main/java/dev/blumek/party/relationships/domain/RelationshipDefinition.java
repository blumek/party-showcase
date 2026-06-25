package dev.blumek.party.relationships.domain;

import static dev.blumek.party.shared.Guards.require;

public record RelationshipDefinition(RelationshipType type, Role fromRole, Role toRole) {

    public RelationshipDefinition {
        require(type != null, "Relationship definition requires a type");
        require(fromRole != null, "Relationship definition requires a from role");
        require(toRole != null, "Relationship definition requires a to role");
    }

    public static RelationshipDefinition of(final RelationshipType type, final Role fromRole, final Role toRole) {
        return new RelationshipDefinition(type, fromRole, toRole);
    }

    public boolean matches(final Endpoint from, final Endpoint to) {
        return fromRole.equals(from.role()) && toRole.equals(to.role());
    }
}
