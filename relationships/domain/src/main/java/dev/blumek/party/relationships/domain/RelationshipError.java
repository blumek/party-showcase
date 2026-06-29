package dev.blumek.party.relationships.domain;

public sealed interface RelationshipError {

    record RelationshipNotFound(RelationshipId id) implements RelationshipError {
    }

    record RolesNotAllowed(RelationshipType type, Role from, Role to) implements RelationshipError {
    }
}
