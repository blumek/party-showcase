package dev.blumek.party.capabilities.infrastructure.persistence;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import dev.blumek.party.capabilities.domain.AreaScope;
import dev.blumek.party.capabilities.domain.Capability;
import dev.blumek.party.capabilities.domain.CapabilityId;
import dev.blumek.party.capabilities.domain.CapabilityKind;
import dev.blumek.party.capabilities.domain.CapabilityPortfolio;
import dev.blumek.party.capabilities.domain.EffectivePeriod;
import dev.blumek.party.capabilities.domain.GradeScope;
import dev.blumek.party.capabilities.domain.ScheduleScope;
import dev.blumek.party.capabilities.domain.VolumePeriod;
import dev.blumek.party.capabilities.domain.VolumeScope;
import dev.blumek.party.shared.OwnerId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = NONE)
@Testcontainers
@ActiveProfiles("jdbc")
@TestPropertySource(properties = "spring.flyway.locations=classpath:db/migration/capability")
@Import({JdbcCapabilityRepository.class, CapabilityRecordMapper.class})
class JdbcCapabilityRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private JdbcCapabilityRepository repository;

    private final OwnerId owner = OwnerId.random();

    @Test
    void roundTripsACapabilityWithEveryScopeKind() {
        var portfolio = givenPortfolioWithRichCapability();

        var actual = savedAndReloaded(portfolio);

        assertThat(actual.capabilities()).hasSize(1);
        assertThat(actual.capabilities().getFirst().scopes())
                .extracting(scope -> scope.dimension())
                .containsExactlyInAnyOrder("AREA", "GRADE", "VOLUME", "SCHEDULE");
    }

    @Test
    void preservesGradeAndVolumeScopeDetails() {
        var portfolio = givenPortfolioWithRichCapability();

        var actual = savedAndReloaded(portfolio);

        var capability = actual.capabilities().getFirst();
        assertThat(capability.scopeOf("GRADE")).get().isEqualTo(GradeScope.SENIOR);
        assertThat(capability.scopeOf("VOLUME")).get().isEqualTo(new VolumeScope(100, VolumePeriod.MONTHLY));
    }

    @Test
    void preservesScheduleDaysAndHours() {
        var portfolio = givenPortfolioWithRichCapability();

        var actual = savedAndReloaded(portfolio);

        var schedule = (ScheduleScope) actual.capabilities().getFirst().scopeOf("SCHEDULE").orElseThrow();
        assertThat(schedule.days()).containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.TUESDAY);
        assertThat(schedule.opensAt()).isEqualTo(LocalTime.of(9, 0));
        assertThat(schedule.closesAt()).isEqualTo(LocalTime.of(17, 0));
    }

    @Test
    void assignsTheInitialVersionOnInsert() {
        var portfolio = givenPortfolioWithRichCapability();

        var actual = savedAndReloaded(portfolio);

        assertThat(actual.version().number()).isEqualTo(1L);
    }

    @Test
    void rejectsAStaleConcurrentSave() {
        repository.save(givenPortfolioWithRichCapability());
        var first = reloaded();
        var second = reloaded();
        repository.save(first);

        var actualThrown = catchThrowable(() -> repository.save(second));

        thenOptimisticLockingFailed(actualThrown);
    }

    private CapabilityPortfolio givenPortfolioWithRichCapability() {
        var portfolio = CapabilityPortfolio.openFor(owner);
        portfolio.grant(new Capability(CapabilityId.random(), CapabilityKind.of("WELDING"),
                Set.of(new AreaScope(Set.of("EU", "US")),
                        GradeScope.SENIOR,
                        new VolumeScope(100, VolumePeriod.MONTHLY),
                        new ScheduleScope(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                                LocalTime.of(9, 0), LocalTime.of(17, 0))),
                EffectivePeriod.always()));
        return portfolio;
    }

    private CapabilityPortfolio savedAndReloaded(final CapabilityPortfolio portfolio) {
        repository.save(portfolio);
        return repository.findByOwner(portfolio.owner()).orElseThrow();
    }

    private CapabilityPortfolio reloaded() {
        return repository.findByOwner(owner).orElseThrow();
    }

    private void thenOptimisticLockingFailed(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(OptimisticLockingFailureException.class);
    }
}
