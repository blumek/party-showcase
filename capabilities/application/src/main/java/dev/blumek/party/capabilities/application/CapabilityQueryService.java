package dev.blumek.party.capabilities.application;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


import dev.blumek.party.capabilities.domain.AreaScope;
import dev.blumek.party.capabilities.domain.AssetScope;
import dev.blumek.party.capabilities.domain.Capability;
import dev.blumek.party.capabilities.domain.CapabilityId;
import dev.blumek.party.capabilities.domain.CapabilityNeed;
import dev.blumek.party.capabilities.domain.CapabilityScope;
import dev.blumek.party.capabilities.domain.CatalogScope;
import dev.blumek.party.capabilities.domain.GradeScope;
import dev.blumek.party.capabilities.domain.ScheduleScope;
import dev.blumek.party.capabilities.domain.StandardScope;
import dev.blumek.party.capabilities.domain.VolumeScope;
import dev.blumek.party.shared.OwnerId;

public class CapabilityQueryService {

    private final CapabilityRepository repository;

    public CapabilityQueryService(final CapabilityRepository repository) {
        this.repository = repository;
    }

    public List<CapabilitySummary> findByOwner(final OwnerId owner) {
        return repository.findByOwner(owner)
                .map(portfolio -> portfolio.capabilities().stream()
                        .map(capability -> summarise(owner, capability))
                        .toList())
                .orElseGet(List::of);
    }

    public Optional<CapabilitySummary> findById(final OwnerId owner, final CapabilityId id) {
        return repository.findByOwner(owner)
                .flatMap(portfolio -> portfolio.find(id))
                .map(capability -> summarise(owner, capability));
    }

    public List<String> findHoldersSatisfying(final CapabilityNeed need) {
        return repository.findAll().stream()
                .filter(portfolio -> portfolio.capabilities().stream()
                        .anyMatch(capability -> capability.isActiveNow() && capability.satisfies(need)))
                .map(portfolio -> portfolio.owner().asString())
                .toList();
    }

    private static CapabilitySummary summarise(final OwnerId owner, final Capability capability) {
        return new CapabilitySummary(
                capability.id().asString(),
                owner.asString(),
                capability.kind().asString(),
                scopeSummaries(capability),
                capability.validity().from(),
                capability.validity().to());
    }

    private static Set<CapabilitySummary.ScopeSummary> scopeSummaries(final Capability capability) {
        return capability.scopes().stream()
                .map(scope -> new CapabilitySummary.ScopeSummary(scope.dimension(), render(scope)))
                .collect(Collectors.toSet());
    }

    private static String render(final CapabilityScope scope) {
        return switch (scope) {
            case AreaScope area -> joined(area.areas());
            case GradeScope grade -> grade.label();
            case VolumeScope volume -> volume.cap() + "/" + volume.period().name();
            case ScheduleScope schedule -> renderSchedule(schedule);
            case StandardScope standard -> joined(standard.standards());
            case CatalogScope catalog -> joined(catalog.items());
            case AssetScope asset -> joined(asset.assets());
        };
    }

    private static String renderSchedule(final ScheduleScope schedule) {
        final var days = schedule.days().stream().map(Enum::name).sorted().collect(Collectors.joining(","));
        return days + " " + schedule.opensAt() + "-" + schedule.closesAt();
    }

    private static String joined(final Set<String> values) {
        return String.join(",", new TreeSet<>(values));
    }
}
