package dev.blumek.party.capabilities.application;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import dev.blumek.party.capabilities.domain.AreaScope;
import dev.blumek.party.capabilities.domain.Capability;
import dev.blumek.party.capabilities.domain.CapabilityId;
import dev.blumek.party.capabilities.domain.CapabilityKind;
import dev.blumek.party.capabilities.domain.CapabilityNeed;
import dev.blumek.party.capabilities.domain.CapabilityPortfolio;
import dev.blumek.party.capabilities.domain.CapabilityScope;
import dev.blumek.party.capabilities.domain.EffectivePeriod;
import dev.blumek.party.capabilities.domain.GradeScope;
import dev.blumek.party.shared.OwnerId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CapabilityQueryServiceTest {

    private final CapabilityRepository repository = mock(CapabilityRepository.class);
    private final CapabilityQueryService service = new CapabilityQueryService(repository);

    private final OwnerId owner = OwnerId.random();
    private final CapabilityKind imaging = CapabilityKind.of("MedicalImaging");

    @Test
    void summarisesACapabilityWithItsKind() {
        var stored = givenStored(capability(imaging, GradeScope.SENIOR));

        var actualSummary = service.findById(owner, stored.id()).orElseThrow();

        thenKindIs(actualSummary, "MedicalImaging");
    }

    private Capability capability(final CapabilityKind kind, final CapabilityScope... scopes) {
        return new Capability(CapabilityId.random(), kind, Set.of(scopes),
                EffectivePeriod.from(LocalDate.of(2026, 1, 1)));
    }

    private Capability givenStored(final Capability... capabilities) {
        var portfolio = CapabilityPortfolio.openFor(owner);
        for (final var capability : capabilities) {
            portfolio.grant(capability);
        }
        when(repository.findByOwner(owner)).thenReturn(Optional.of(portfolio));
        return capabilities[0];
    }

    private void thenKindIs(final CapabilitySummary summary, final String expected) {
        assertThat(summary.kind()).isEqualTo(expected);
    }

    @Test
    void rendersScopesAsDimensionedValues() {
        var stored = givenStored(capability(imaging, GradeScope.SENIOR));

        var actualSummary = service.findById(owner, stored.id()).orElseThrow();

        thenScopesContain(actualSummary, new CapabilitySummary.ScopeSummary("GRADE", "SENIOR"));
    }

    private void thenScopesContain(final CapabilitySummary summary, final CapabilitySummary.ScopeSummary expected) {
        assertThat(summary.scopes()).contains(expected);
    }

    @Test
    void includesValidityBounds() {
        var stored = givenStored(capability(imaging, GradeScope.SENIOR));

        var actualSummary = service.findById(owner, stored.id()).orElseThrow();

        thenValidFromIs(actualSummary, LocalDate.of(2026, 1, 1));
    }

    private void thenValidFromIs(final CapabilitySummary summary, final LocalDate expected) {
        assertThat(summary.validFrom()).isEqualTo(expected);
        assertThat(summary.validTo()).isNull();
    }

    @Test
    void returnsEmptyForAnUnknownCapability() {
        givenStored(capability(imaging, GradeScope.SENIOR));

        var actualSummary = service.findById(owner, CapabilityId.random());

        thenAbsent(actualSummary);
    }

    private void thenAbsent(final Optional<CapabilitySummary> summary) {
        assertThat(summary).isEmpty();
    }

    @Test
    void returnsAnEmptyListForAnUnknownOwner() {
        when(repository.findByOwner(owner)).thenReturn(Optional.empty());

        var actualSummaries = service.findByOwner(owner);

        thenCountIs(actualSummaries.size(), 0);
    }

    private void thenCountIs(final int actual, final int expected) {
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findsHoldersWhoseActiveCapabilitySatisfiesTheNeed() {
        givenAllPortfolios(portfolioFor(owner, capability(imaging, new AreaScope(Set.of("PL", "DE")))));
        var need = new CapabilityNeed(imaging, Set.of(new AreaScope(Set.of("PL"))));

        var actualHolders = service.findHoldersSatisfying(need);

        thenHoldersAre(actualHolders, owner);
    }

    private void givenAllPortfolios(final CapabilityPortfolio... portfolios) {
        when(repository.findAll()).thenReturn(List.of(portfolios));
    }

    private CapabilityPortfolio portfolioFor(final OwnerId holder, final Capability... capabilities) {
        var portfolio = CapabilityPortfolio.openFor(holder);
        for (final var capability : capabilities) {
            portfolio.grant(capability);
        }
        return portfolio;
    }

    private void thenHoldersAre(final List<String> holders, final OwnerId... expected) {
        var ids = Arrays.stream(expected).map(OwnerId::asString).toList();
        assertThat(holders).containsExactlyInAnyOrderElementsOf(ids);
    }

    @Test
    void excludesHoldersWhoseMatchingCapabilityIsExpired() {
        var expired = new Capability(CapabilityId.random(), imaging, Set.of(new AreaScope(Set.of("PL"))),
                EffectivePeriod.until(LocalDate.of(2020, 1, 1)));
        givenAllPortfolios(portfolioFor(owner, expired));
        var need = new CapabilityNeed(imaging, Set.of(new AreaScope(Set.of("PL"))));

        var actualHolders = service.findHoldersSatisfying(need);

        thenNoHolders(actualHolders);
    }

    private void thenNoHolders(final List<String> holders) {
        assertThat(holders).isEmpty();
    }
}
