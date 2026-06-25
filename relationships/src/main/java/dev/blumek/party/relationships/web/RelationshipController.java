package dev.blumek.party.relationships.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.blumek.party.relationships.application.EstablishRelationship;
import dev.blumek.party.relationships.application.RelationshipQueryService;
import dev.blumek.party.relationships.application.RelationshipService;
import dev.blumek.party.relationships.application.RelationshipSummary;
import dev.blumek.party.relationships.application.TerminateRelationship;
import dev.blumek.party.relationships.domain.RelationshipError;
import dev.blumek.party.relationships.domain.RelationshipId;
import dev.blumek.party.relationships.domain.RelationshipPeriod;
import dev.blumek.party.relationships.domain.RelationshipType;
import dev.blumek.party.relationships.domain.Role;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;

@RestController
@RequestMapping("/parties/{partyId}/relationships")
class RelationshipController {

    private final RelationshipService relationshipService;
    private final RelationshipQueryService queryService;

    RelationshipController(final RelationshipService relationshipService, final RelationshipQueryService queryService) {
        this.relationshipService = relationshipService;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<RelationshipSummary> establish(@PathVariable("partyId") final OwnerId owner,
            @RequestBody final EstablishRelationshipRequest request) {
        final var command = new EstablishRelationship(owner, OwnerId.of(request.to()),
                Role.of(request.fromRole()), Role.of(request.toRole()), relationshipId(request.relationshipId()),
                RelationshipType.of(request.type()), validity(request.validFrom(), request.validTo()));
        return respond(owner, relationshipService.establish(command));
    }

    @GetMapping
    public List<RelationshipSummary> findAll(@PathVariable("partyId") final OwnerId owner) {
        return queryService.findByOwner(owner);
    }

    @GetMapping("/{relationshipId}")
    public ResponseEntity<RelationshipSummary> findById(@PathVariable("partyId") final OwnerId owner,
            @PathVariable final RelationshipId relationshipId) {
        return queryService.findById(owner, relationshipId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{relationshipId}")
    public ResponseEntity<Void> terminate(@PathVariable("partyId") final OwnerId owner,
            @PathVariable final RelationshipId relationshipId) {
        return relationshipService.terminate(new TerminateRelationship(owner, relationshipId))
                .fold(error -> ResponseEntity.status(statusFor(error)).build(),
                        id -> ResponseEntity.noContent().build());
    }

    private ResponseEntity<RelationshipSummary> respond(final OwnerId owner,
            final Result<RelationshipError, RelationshipId> result) {
        return result.fold(this::failure, id -> success(owner, id));
    }

    private ResponseEntity<RelationshipSummary> success(final OwnerId owner, final RelationshipId id) {
        return queryService.findById(owner, id)
                .map(summary -> ResponseEntity.status(HttpStatus.CREATED).body(summary))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<RelationshipSummary> failure(final RelationshipError error) {
        return ResponseEntity.status(statusFor(error)).build();
    }

    private static RelationshipId relationshipId(final String raw) {
        return raw == null ? null : RelationshipId.of(raw);
    }

    private static RelationshipPeriod validity(final LocalDate from, final LocalDate to) {
        return new RelationshipPeriod(from, to);
    }

    private static HttpStatus statusFor(final RelationshipError error) {
        return switch (error) {
            case RelationshipError.RelationshipNotFound _ -> HttpStatus.NOT_FOUND;
            case RelationshipError.RolesNotAllowed _ -> HttpStatus.CONFLICT;
        };
    }
}
