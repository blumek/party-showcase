package dev.blumek.party.capabilities.domain;

import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CapabilityNeedTest {

    private final CapabilityKind imaging = CapabilityKind.of("MedicalImaging");

    @Test
    void isSatisfiedWhenKindAndAllScopesMatch() {
        var capability = givenCapability(imaging, new AreaScope(Set.of("PL", "DE")), GradeScope.SENIOR);
        var need = new CapabilityNeed(imaging, Set.of(new AreaScope(Set.of("PL")), GradeScope.REGULAR));

        var actualSatisfied = need.satisfiedBy(capability);

        thenTrue(actualSatisfied);
    }

    private Capability givenCapability(final CapabilityKind kind, final CapabilityScope... scopes) {
        return new Capability(CapabilityId.random(), kind, Set.of(scopes), EffectivePeriod.always());
    }

    private void thenTrue(final boolean actual) {
        assertThat(actual).isTrue();
    }

    @Test
    void isNotSatisfiedWhenTheKindDiffers() {
        var capability = givenCapability(CapabilityKind.of("GoodsDelivery"), new AreaScope(Set.of("PL")));
        var need = new CapabilityNeed(imaging, Set.of(new AreaScope(Set.of("PL"))));

        var actualSatisfied = need.satisfiedBy(capability);

        thenFalse(actualSatisfied);
    }

    private void thenFalse(final boolean actual) {
        assertThat(actual).isFalse();
    }

    @Test
    void isNotSatisfiedWhenARequiredScopeIsMissing() {
        var capability = givenCapability(imaging, new AreaScope(Set.of("PL")));
        var need = new CapabilityNeed(imaging, Set.of(GradeScope.SENIOR));

        var actualSatisfied = need.satisfiedBy(capability);

        thenFalse(actualSatisfied);
    }

    @Test
    void isNotSatisfiedWhenARequiredScopeIsTooBroad() {
        var capability = givenCapability(imaging, new AreaScope(Set.of("PL")));
        var need = new CapabilityNeed(imaging, Set.of(new AreaScope(Set.of("PL", "DE"))));

        var actualSatisfied = need.satisfiedBy(capability);

        thenFalse(actualSatisfied);
    }
}
