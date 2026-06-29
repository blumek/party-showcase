package dev.blumek.party.capabilities.domain;

import java.util.Set;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CapabilityPortfolioTest {

    private final OwnerId owner = OwnerId.random();
    private final CapabilityKind imaging = CapabilityKind.of("MedicalImaging");

    @Test
    void grantingANewCapabilityStoresIt() {
        var portfolio = CapabilityPortfolio.openFor(owner);
        var capability = givenCapability();

        portfolio.grant(capability);

        thenPortfolioContains(portfolio, capability);
    }

    private Capability givenCapability() {
        return new Capability(CapabilityId.random(), imaging, Set.of(GradeScope.SENIOR), EffectivePeriod.always());
    }

    private void thenPortfolioContains(final CapabilityPortfolio portfolio, final Capability capability) {
        assertThat(portfolio.capabilities()).contains(capability);
    }

    @Test
    void grantingANewCapabilityRaisesCapabilityGranted() {
        var portfolio = CapabilityPortfolio.openFor(owner);
        var capability = givenCapability();

        portfolio.grant(capability);

        thenEventsAre(portfolio, new CapabilityGranted(owner, capability.id(), imaging));
    }

    private void thenEventsAre(final CapabilityPortfolio portfolio, final DomainEvent... expected) {
        assertThat(portfolio.domainEvents()).containsExactly(expected);
    }

    @Test
    void revisingAnExistingCapabilityRaisesCapabilityRevised() {
        var portfolio = givenPortfolioHolding(givenCapability());
        var stored = portfolio.capabilities().getFirst();

        portfolio.grant(new Capability(stored.id(), imaging, Set.of(GradeScope.MASTER), stored.validity()));

        thenEventsAre(portfolio, new CapabilityRevised(owner, stored.id(), imaging));
    }

    private CapabilityPortfolio givenPortfolioHolding(final Capability capability) {
        var portfolio = CapabilityPortfolio.openFor(owner);
        portfolio.grant(capability);
        portfolio.clearDomainEvents();
        return portfolio;
    }

    @Test
    void grantingIdenticalContentRaisesNoEvent() {
        var portfolio = givenPortfolioHolding(givenCapability());
        var stored = portfolio.capabilities().getFirst();

        portfolio.grant(new Capability(stored.id(), stored.kind(), stored.scopes(), stored.validity()));

        thenNoEventsRaised(portfolio);
    }

    private void thenNoEventsRaised(final CapabilityPortfolio portfolio) {
        assertThat(portfolio.domainEvents()).isEmpty();
    }

    @Test
    void revokingAKnownCapabilityRaisesCapabilityRevoked() {
        var portfolio = givenPortfolioHolding(givenCapability());
        var stored = portfolio.capabilities().getFirst();

        portfolio.revoke(stored.id());

        thenEventsAre(portfolio, new CapabilityRevoked(owner, stored.id(), imaging));
    }

    @Test
    void revokingAnUnknownCapabilityReturnsCapabilityNotFound() {
        var portfolio = CapabilityPortfolio.openFor(owner);

        var actualResult = portfolio.revoke(CapabilityId.random());

        thenFailedWith(actualResult, CapabilityError.CapabilityNotFound.class);
    }

    private void thenFailedWith(final Result<CapabilityError, CapabilityId> result,
            final Class<? extends CapabilityError> expected) {
        final CapabilityError actualError = result.fold(error -> error, id -> null);
        assertThat(actualError).isInstanceOf(expected);
    }
}
