package dev.blumek.party.relationships.application;

import java.util.List;

public interface RelationshipFinder {

    List<RelationshipSummary> find(RelationshipQuery query);
}
