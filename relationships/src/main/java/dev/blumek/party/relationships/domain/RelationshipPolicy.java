package dev.blumek.party.relationships.domain;

import java.util.Optional;
import java.util.Set;

@FunctionalInterface
public interface RelationshipPolicy {

    RelationshipPolicy DEFAULT = requiring(Set.of(
            RelationshipDefinition.of(RelationshipType.of("Employment"), Role.of("Employer"), Role.of("Employee")),
            RelationshipDefinition.of(RelationshipType.of("Membership"), Role.of("Organization"), Role.of("Member"))));

    Optional<RelationshipError> check(Endpoint from, Endpoint to, RelationshipType type);

    static RelationshipPolicy permitAll() {
        return (from, to, type) -> Optional.empty();
    }

    static RelationshipPolicy requiring(final Set<RelationshipDefinition> definitions) {
        final var declared = Set.copyOf(definitions);
        return (from, to, type) -> {
            final var forType = declared.stream().filter(definition -> definition.type().equals(type)).toList();
            if (forType.isEmpty() || forType.stream().anyMatch(definition -> definition.matches(from, to))) {
                return Optional.empty();
            }
            return Optional.of(new RelationshipError.RolesNotAllowed(type, from.role(), to.role()));
        };
    }
}
