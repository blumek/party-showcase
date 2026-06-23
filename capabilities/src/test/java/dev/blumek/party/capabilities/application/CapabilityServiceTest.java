package dev.blumek.party.capabilities.application;

import java.util.Optional;
import java.util.Set;

import dev.blumek.party.capabilities.domain.Capability;
import dev.blumek.party.capabilities.domain.CapabilityError;
import dev.blumek.party.capabilities.domain.CapabilityId;
import dev.blumek.party.capabilities.domain.CapabilityKind;
import dev.blumek.party.capabilities.domain.CapabilityPortfolio;
import dev.blumek.party.capabilities.domain.EffectivePeriod;
import dev.blumek.party.capabilities.domain.GradeScope;
import dev.blumek.party.shared.DomainEventPublisher;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CapabilityServiceTest {

    private final CapabilityRepository repository = mock(CapabilityRepository.class);
    private final DomainEventPublisher publisher = mock(DomainEventPublisher.class);
    private final CapabilityService service = new CapabilityService(repository, publisher);

    private final OwnerId owner = OwnerId.random();
    private final CapabilityKind imaging = CapabilityKind.of("MedicalImaging");

    @Test
    void grantingForANewOwnerPersistsThePortfolio() {
        givenNoPortfolioFor(owner);

        service.grant(grantCommand());

        thenAPortfolioWasSaved();
    }

    private void givenNoPortfolioFor(final OwnerId owner) {
        when(repository.findByOwner(owner)).thenReturn(Optional.empty());
    }

    private GrantCapability grantCommand() {
        return new GrantCapability(owner, null, imaging, Set.of(GradeScope.SENIOR), EffectivePeriod.always());
    }

    private void thenAPortfolioWasSaved() {
        verify(repository).save(any(CapabilityPortfolio.class));
    }

    @Test
    void grantingReturnsTheNewCapabilityId() {
        givenNoPortfolioFor(owner);

        var actualResult = service.grant(grantCommand());

        thenSucceeded(actualResult);
    }

    private void thenSucceeded(final Result<CapabilityError, CapabilityId> result) {
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void grantingPublishesTheRaisedEvents() {
        givenNoPortfolioFor(owner);

        service.grant(grantCommand());

        thenEventsWerePublished();
    }

    private void thenEventsWerePublished() {
        verify(publisher).publishAll(any());
    }

    @Test
    void revokingFromAnUnknownOwnerReturnsCapabilityNotFound() {
        givenNoPortfolioFor(owner);

        var actualResult = service.revoke(new RevokeCapability(owner, CapabilityId.random()));

        thenFailedWith(actualResult, CapabilityError.CapabilityNotFound.class);
    }

    private void thenFailedWith(final Result<CapabilityError, CapabilityId> result,
            final Class<? extends CapabilityError> expected) {
        final CapabilityError actualError = result.fold(error -> error, id -> null);
        assertThat(actualError).isInstanceOf(expected);
    }

    @Test
    void revokingFromAnUnknownOwnerPersistsNothing() {
        givenNoPortfolioFor(owner);

        service.revoke(new RevokeCapability(owner, CapabilityId.random()));

        thenNothingWasSaved();
    }

    private void thenNothingWasSaved() {
        verify(repository, never()).save(any());
    }

    @Test
    void revokingAKnownCapabilityPersistsThePortfolio() {
        var stored = givenStoredCapability();
        givenPortfolioHolding(stored);

        service.revoke(new RevokeCapability(owner, stored.id()));

        thenAPortfolioWasSaved();
    }

    private Capability givenStoredCapability() {
        return new Capability(CapabilityId.random(), imaging, Set.of(GradeScope.SENIOR), EffectivePeriod.always());
    }

    private void givenPortfolioHolding(final Capability capability) {
        var portfolio = CapabilityPortfolio.openFor(owner);
        portfolio.grant(capability);
        portfolio.clearDomainEvents();
        when(repository.findByOwner(owner)).thenReturn(Optional.of(portfolio));
    }
}
