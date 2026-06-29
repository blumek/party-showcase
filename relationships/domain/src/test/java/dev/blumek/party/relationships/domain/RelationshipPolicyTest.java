package dev.blumek.party.relationships.domain;

import java.util.Optional;
import java.util.Set;

import dev.blumek.party.shared.OwnerId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RelationshipPolicyTest {

    private final RelationshipType employment = RelationshipType.of("Employment");
    private final RelationshipPolicy policy = RelationshipPolicy.requiring(Set.of(
            RelationshipDefinition.of(employment, Role.of("Employer"), Role.of("Employee"))));

    @Test
    void allowsEndpointsThatMatchADefinition() {
        var actualError = policy.check(
                Endpoint.of(OwnerId.random(), Role.of("Employer")),
                Endpoint.of(OwnerId.random(), Role.of("Employee")), employment);

        thenAllowed(actualError);
    }

    private void thenAllowed(final Optional<RelationshipError> actual) {
        assertThat(actual).isEmpty();
    }

    @Test
    void rejectsMismatchedRolesWithRolesNotAllowed() {
        var actualError = policy.check(
                Endpoint.of(OwnerId.random(), Role.of("Employee")),
                Endpoint.of(OwnerId.random(), Role.of("Employer")), employment);

        thenRejectedWith(actualError, RelationshipError.RolesNotAllowed.class);
    }

    private void thenRejectedWith(final Optional<RelationshipError> actual,
            final Class<? extends RelationshipError> expected) {
        assertThat(actual).get().isInstanceOf(expected);
    }

    @Test
    void allowsAnyRolesForATypeWithoutADefinition() {
        var actualError = policy.check(
                Endpoint.of(OwnerId.random(), Role.of("Buyer")),
                Endpoint.of(OwnerId.random(), Role.of("Seller")), RelationshipType.of("Trade"));

        thenAllowed(actualError);
    }
}
