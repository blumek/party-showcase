package dev.blumek.party.capabilities.infrastructure.persistence;

import java.time.DayOfWeek;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import dev.blumek.party.capabilities.domain.AreaScope;
import dev.blumek.party.capabilities.domain.AssetScope;
import dev.blumek.party.capabilities.domain.Capability;
import dev.blumek.party.capabilities.domain.CapabilityId;
import dev.blumek.party.capabilities.domain.CapabilityKind;
import dev.blumek.party.capabilities.domain.CapabilityPortfolio;
import dev.blumek.party.capabilities.domain.CapabilityScope;
import dev.blumek.party.capabilities.domain.CatalogScope;
import dev.blumek.party.capabilities.domain.EffectivePeriod;
import dev.blumek.party.capabilities.domain.GradeScope;
import dev.blumek.party.capabilities.domain.ScheduleScope;
import dev.blumek.party.capabilities.domain.StandardScope;
import dev.blumek.party.capabilities.domain.VolumePeriod;
import dev.blumek.party.capabilities.domain.VolumeScope;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Version;

@Component
@Profile("jdbc")
class CapabilityRecordMapper {

    CapabilityPortfolioRecord toRecord(final CapabilityPortfolio portfolio) {
        final var capabilities = portfolio.capabilities().stream()
                .map(CapabilityRecordMapper::capabilityRecord)
                .collect(Collectors.toSet());
        return new CapabilityPortfolioRecord(portfolio.owner().value(), portfolio.version().number(), capabilities);
    }

    CapabilityPortfolio toDomain(final CapabilityPortfolioRecord entity) {
        final var capabilities = entity.capabilities().stream()
                .map(CapabilityRecordMapper::capability)
                .toList();
        return CapabilityPortfolio.rehydrate(new OwnerId(entity.ownerId()), capabilities, new Version(entity.version()));
    }

    private static CapabilityRecord capabilityRecord(final Capability capability) {
        final var scopes = capability.scopes().stream()
                .map(CapabilityRecordMapper::scopeRecord)
                .collect(Collectors.toSet());
        return new CapabilityRecord(capability.id().value(), capability.kind().asString(),
                capability.validity().from(), capability.validity().to(), scopes);
    }

    private static CapabilityScopeRecord scopeRecord(final CapabilityScope scope) {
        return switch (scope) {
            case AreaScope area -> tagScope("AREA", area.areas());
            case StandardScope standard -> tagScope("STANDARD", standard.standards());
            case CatalogScope catalog -> tagScope("CATALOG", catalog.items());
            case AssetScope asset -> tagScope("ASSET", asset.assets());
            case GradeScope grade -> new CapabilityScopeRecord(UUID.randomUUID(), "GRADE",
                    grade.label(), grade.rank(), null, null, null, null, Set.of(), Set.of());
            case VolumeScope volume -> new CapabilityScopeRecord(UUID.randomUUID(), "VOLUME",
                    null, null, volume.cap(), volume.period().name(), null, null, Set.of(), Set.of());
            case ScheduleScope schedule -> new CapabilityScopeRecord(UUID.randomUUID(), "SCHEDULE",
                    null, null, null, null, schedule.opensAt(), schedule.closesAt(), Set.of(), dayRecords(schedule));
        };
    }

    private static CapabilityScopeRecord tagScope(final String dimension, final Set<String> values) {
        final var valueRecords = values.stream()
                .map(CapabilityScopeValueRecord::new)
                .collect(Collectors.toSet());
        return new CapabilityScopeRecord(UUID.randomUUID(), dimension,
                null, null, null, null, null, null, valueRecords, Set.of());
    }

    private static Set<CapabilityScopeDayRecord> dayRecords(final ScheduleScope schedule) {
        return schedule.days().stream()
                .map(day -> new CapabilityScopeDayRecord(day.name()))
                .collect(Collectors.toSet());
    }

    private static Capability capability(final CapabilityRecord entity) {
        final var scopes = entity.scopes().stream()
                .map(CapabilityRecordMapper::scope)
                .collect(Collectors.toSet());
        return new Capability(new CapabilityId(entity.id()), CapabilityKind.of(entity.kind()), scopes,
                new EffectivePeriod(entity.validFrom(), entity.validTo()));
    }

    private static CapabilityScope scope(final CapabilityScopeRecord entity) {
        return switch (entity.dimension()) {
            case "AREA" -> new AreaScope(values(entity));
            case "STANDARD" -> new StandardScope(values(entity));
            case "CATALOG" -> new CatalogScope(values(entity));
            case "ASSET" -> new AssetScope(values(entity));
            case "GRADE" -> new GradeScope(entity.gradeLabel(), entity.gradeRank());
            case "VOLUME" -> new VolumeScope(entity.volumeCap(), VolumePeriod.valueOf(entity.volumePeriod()));
            case "SCHEDULE" -> new ScheduleScope(days(entity), entity.opensAt(), entity.closesAt());
            default -> throw new IllegalStateException("Unknown scope dimension: " + entity.dimension());
        };
    }

    private static Set<String> values(final CapabilityScopeRecord entity) {
        return entity.values().stream()
                .map(CapabilityScopeValueRecord::value)
                .collect(Collectors.toSet());
    }

    private static Set<DayOfWeek> days(final CapabilityScopeRecord entity) {
        return entity.days().stream()
                .map(day -> DayOfWeek.valueOf(day.day()))
                .collect(Collectors.toSet());
    }
}
