package dev.blumek.party.relationships.domain;

import dev.blumek.party.shared.OwnerId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RelationshipTest {

    private final OwnerId employer = OwnerId.random();
    private final OwnerId employee = OwnerId.random();

    @Test
    void involvesEitherEndpointParty() {
        var relationship = givenEmployment();

        var actualInvolves = relationship.involves(employee);

        thenTrue(actualInvolves);
    }

    private Relationship givenEmployment() {
        return new Relationship(RelationshipId.random(),
                Endpoint.of(employer, Role.of("Employer")),
                Endpoint.of(employee, Role.of("Employee")),
                RelationshipType.of("Employment"), RelationshipPeriod.always());
    }

    private void thenTrue(final boolean actual) {
        assertThat(actual).isTrue();
    }

    @Test
    void doesNotInvolveAnUnrelatedParty() {
        var relationship = givenEmployment();

        var actualInvolves = relationship.involves(OwnerId.random());

        thenFalse(actualInvolves);
    }

    private void thenFalse(final boolean actual) {
        assertThat(actual).isFalse();
    }

    @Test
    void differsFromAnotherWithADifferentType() {
        var relationship = givenEmployment();

        var actualDiffers = relationship.differsFrom(new Relationship(relationship.id(), relationship.from(),
                relationship.to(), RelationshipType.of("Membership"), relationship.validity()));

        thenTrue(actualDiffers);
    }

    @Test
    void doesNotDifferFromAnIdenticalRelationship() {
        var relationship = givenEmployment();

        var actualDiffers = relationship.differsFrom(new Relationship(relationship.id(), relationship.from(),
                relationship.to(), relationship.type(), relationship.validity()));

        thenFalse(actualDiffers);
    }
}
