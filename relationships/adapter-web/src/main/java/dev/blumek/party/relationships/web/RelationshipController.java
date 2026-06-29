package dev.blumek.party.relationships.web;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.blumek.party.relationships.application.EstablishRelationship;
import dev.blumek.party.relationships.application.RelationshipFinder;
import dev.blumek.party.relationships.application.RelationshipQuery;
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
    private final RelationshipFinder finder;

    RelationshipController(final RelationshipService relationshipService, final RelationshipQueryService queryService,
            final RelationshipFinder finder) {
        this.relationshipService = relationshipService;
        this.queryService = queryService;
        this.finder = finder;
    }

    @PostMapping
    public ResponseEntity<Object> establish(@PathVariable("partyId") final OwnerId owner,
            @RequestBody @Valid final EstablishRelationshipRequest request) {
        final var command = new EstablishRelationship(owner, OwnerId.of(request.to()),
                Role.of(request.fromRole()), Role.of(request.toRole()),
                RelationshipType.of(request.type()), validity(request.validFrom(), request.validTo()));
        return respond(owner, relationshipService.establish(command));
    }

    @GetMapping
    public List<RelationshipSummary> findAll(@PathVariable("partyId") final OwnerId owner,
            @RequestParam(defaultValue = "OUTGOING") final RelationshipQuery.Direction direction,
            @RequestParam(required = false) final String type,
            @RequestParam(required = false) final String role) {
        return finder.find(new RelationshipQuery(owner, direction, type, role));
    }

    @GetMapping("/{relationshipId}")
    public ResponseEntity<Object> findById(@PathVariable("partyId") final OwnerId owner,
            @PathVariable final RelationshipId relationshipId) {
        return queryService.findById(owner, relationshipId)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> problem(HttpStatus.NOT_FOUND, "No relationship found with the given id", "RelationshipNotFound"));
    }

    @DeleteMapping("/{relationshipId}")
    public ResponseEntity<Object> terminate(@PathVariable("partyId") final OwnerId owner,
            @PathVariable final RelationshipId relationshipId) {
        return relationshipService.terminate(new TerminateRelationship(owner, relationshipId))
                .fold(this::failure, id -> ResponseEntity.noContent().build());
    }

    private ResponseEntity<Object> respond(final OwnerId owner,
            final Result<RelationshipError, RelationshipId> result) {
        return result.fold(this::failure, id -> success(owner, id));
    }

    private ResponseEntity<Object> success(final OwnerId owner, final RelationshipId id) {
        return queryService.findById(owner, id)
                .<ResponseEntity<Object>>map(summary -> ResponseEntity.status(HttpStatus.CREATED).body(summary))
                .orElseGet(() -> problem(HttpStatus.NOT_FOUND, "No relationship found with the given id", "RelationshipNotFound"));
    }

    private ResponseEntity<Object> failure(final RelationshipError error) {
        return problem(statusFor(error), detailFor(error), error.getClass().getSimpleName());
    }

    private static ResponseEntity<Object> problem(final HttpStatus status, final String detail, final String code) {
        final var body = ProblemDetail.forStatusAndDetail(status, detail);
        body.setProperty("code", code);
        return ResponseEntity.status(status).<Object>body(body);
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

    private static String detailFor(final RelationshipError error) {
        return switch (error) {
            case RelationshipError.RelationshipNotFound _ -> "No relationship found with the given id";
            case RelationshipError.RolesNotAllowed _ -> "The relationship type does not allow the given roles";
        };
    }
}
