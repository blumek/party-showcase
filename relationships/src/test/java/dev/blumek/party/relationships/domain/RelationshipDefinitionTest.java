package dev.blumek.party.relationships.domain;

import dev.blumek.party.shared.OwnerId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RelationshipDefinitionTest {

    private final RelationshipDefinition employment = RelationshipDefinition.of(
            RelationshipType.of("Employment"), Role.of("Employer"), Role.of("Employee"));

    @Test
    void matchesEndpointsThatPlayTheRequiredRoles() {
        var actualMatches = employment.matches(
                Endpoint.of(OwnerId.random(), Role.of("Employer")),
                Endpoint.of(OwnerId.random(), Role.of("Employee")));

        thenTrue(actualMatches);
    }

    private void thenTrue(final boolean actual) {
        assertThat(actual).isTrue();
    }

    @Test
    void doesNotMatchWhenTheRolesAreSwapped() {
        var actualMatches = employment.matches(
                Endpoint.of(OwnerId.random(), Role.of("Employee")),
                Endpoint.of(OwnerId.random(), Role.of("Employer")));

        thenFalse(actualMatches);
    }

    private void thenFalse(final boolean actual) {
        assertThat(actual).isFalse();
    }
}
