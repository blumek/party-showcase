package dev.blumek.party.relationships.application;

import dev.blumek.party.shared.OwnerId;

public record RelationshipQuery(OwnerId owner, Direction direction, String type, String role) {

    public enum Direction {
        OUTGOING,
        INCOMING,
        ANY
    }
}
