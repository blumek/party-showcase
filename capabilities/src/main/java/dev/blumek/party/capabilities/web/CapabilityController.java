package dev.blumek.party.capabilities.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.blumek.party.capabilities.application.CapabilityQueryService;
import dev.blumek.party.capabilities.application.CapabilityService;
import dev.blumek.party.capabilities.application.CapabilitySummary;
import dev.blumek.party.capabilities.application.GrantCapability;
import dev.blumek.party.capabilities.application.RevokeCapability;
import dev.blumek.party.capabilities.domain.AreaScope;
import dev.blumek.party.capabilities.domain.AssetScope;
import dev.blumek.party.capabilities.domain.CapabilityError;
import dev.blumek.party.capabilities.domain.CapabilityId;
import dev.blumek.party.capabilities.domain.CapabilityKind;
import dev.blumek.party.capabilities.domain.CapabilityScope;
import dev.blumek.party.capabilities.domain.CatalogScope;
import dev.blumek.party.capabilities.domain.EffectivePeriod;
import dev.blumek.party.capabilities.domain.GradeScope;
import dev.blumek.party.capabilities.domain.ScheduleScope;
import dev.blumek.party.capabilities.domain.StandardScope;
import dev.blumek.party.capabilities.domain.VolumeScope;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;

import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toUnmodifiableSet;

@RestController
@RequestMapping("/parties/{partyId}/capabilities")
class CapabilityController {

    private final CapabilityService capabilityService;
    private final CapabilityQueryService queryService;

    CapabilityController(final CapabilityService capabilityService, final CapabilityQueryService queryService) {
        this.capabilityService = capabilityService;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<CapabilitySummary> grant(@PathVariable("partyId") final OwnerId owner,
            @RequestBody final GrantCapabilityRequest request) {
        final var command = new GrantCapability(owner, capabilityId(request.capabilityId()),
                CapabilityKind.of(request.kind()), scopes(request.scopes()),
                validity(request.validFrom(), request.validTo()));
        return respond(owner, capabilityService.grant(command));
    }

    @GetMapping
    public List<CapabilitySummary> findAll(@PathVariable("partyId") final OwnerId owner) {
        return queryService.findByOwner(owner);
    }

    @GetMapping("/{capabilityId}")
    public ResponseEntity<CapabilitySummary> findById(@PathVariable("partyId") final OwnerId owner,
            @PathVariable final CapabilityId capabilityId) {
        return queryService.findById(owner, capabilityId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{capabilityId}")
    public ResponseEntity<Void> revoke(@PathVariable("partyId") final OwnerId owner,
            @PathVariable final CapabilityId capabilityId) {
        return capabilityService.revoke(new RevokeCapability(owner, capabilityId))
                .fold(error -> ResponseEntity.status(statusFor(error)).build(),
                        id -> ResponseEntity.noContent().build());
    }

    private ResponseEntity<CapabilitySummary> respond(final OwnerId owner,
            final Result<CapabilityError, CapabilityId> result) {
        return result.fold(this::failure, id -> success(owner, id));
    }

    private ResponseEntity<CapabilitySummary> success(final OwnerId owner, final CapabilityId id) {
        return queryService.findById(owner, id)
                .map(summary -> ResponseEntity.status(HttpStatus.CREATED).body(summary))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<CapabilitySummary> failure(final CapabilityError error) {
        return ResponseEntity.status(statusFor(error)).build();
    }

    private static CapabilityId capabilityId(final String raw) {
        return raw == null ? null : CapabilityId.of(raw);
    }

    private static EffectivePeriod validity(final LocalDate from, final LocalDate to) {
        return new EffectivePeriod(from, to);
    }

    private static Set<CapabilityScope> scopes(final List<ScopeRequest> requests) {
        return requests == null ? Set.of()
                : requests.stream().map(CapabilityController::toScope).collect(toUnmodifiableSet());
    }

    private static CapabilityScope toScope(final ScopeRequest request) {
        return switch (request.dimension().toUpperCase(ROOT)) {
            case "AREA" -> new AreaScope(request.values());
            case "GRADE" -> new GradeScope(request.label(), request.rank());
            case "VOLUME" -> new VolumeScope(request.cap(), request.period());
            case "SCHEDULE" -> new ScheduleScope(request.days(), request.opensAt(), request.closesAt());
            case "STANDARD" -> new StandardScope(request.values());
            case "CATALOG" -> new CatalogScope(request.values());
            case "ASSET" -> new AssetScope(request.values());
            default -> throw new IllegalArgumentException("Unknown scope dimension: " + request.dimension());
        };
    }

    private static HttpStatus statusFor(final CapabilityError error) {
        return switch (error) {
            case CapabilityError.CapabilityNotFound _ -> HttpStatus.NOT_FOUND;
        };
    }
}
