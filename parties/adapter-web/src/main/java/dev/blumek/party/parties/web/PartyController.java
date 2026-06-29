package dev.blumek.party.parties.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.blumek.party.parties.application.AssignRole;
import dev.blumek.party.parties.application.PartyQueryService;
import dev.blumek.party.parties.application.PartySearchCriteria;
import dev.blumek.party.parties.application.PartyService;
import dev.blumek.party.parties.application.PartySummary;
import dev.blumek.party.parties.application.RegisterCompany;
import dev.blumek.party.parties.application.RegisterIdentifier;
import dev.blumek.party.parties.application.RegisterOrganizationUnit;
import dev.blumek.party.parties.application.RegisterPerson;
import dev.blumek.party.parties.domain.LegalName;
import dev.blumek.party.parties.domain.NationalIdentificationNumber;
import dev.blumek.party.parties.domain.OfficialIdentifier;
import dev.blumek.party.parties.domain.PartyError;
import dev.blumek.party.parties.domain.PartyId;
import dev.blumek.party.parties.domain.PassportNumber;
import dev.blumek.party.parties.domain.PersonName;
import dev.blumek.party.parties.domain.PersonProfile;
import dev.blumek.party.parties.domain.Role;
import dev.blumek.party.parties.domain.TaxIdentificationNumber;
import dev.blumek.party.shared.Result;

import static java.util.Locale.ROOT;

@RestController
@RequestMapping("/parties")
class PartyController {

    private final PartyService partyService;
    private final PartyQueryService queryService;

    PartyController(final PartyService partyService, final PartyQueryService queryService) {
        this.partyService = partyService;
        this.queryService = queryService;
    }

    @PostMapping("/people")
    public ResponseEntity<Object> registerPerson(@RequestBody @Valid final RegisterPersonRequest request) {
        final var profile = new PersonProfile(new PersonName(request.given(), request.family()), request.dateOfBirth());
        return respond(partyService.register(new RegisterPerson(profile)), HttpStatus.CREATED);
    }

    @PostMapping("/companies")
    public ResponseEntity<Object> registerCompany(@RequestBody @Valid final RegisterOrganizationRequest request) {
        return respond(partyService.register(new RegisterCompany(new LegalName(request.name()))), HttpStatus.CREATED);
    }

    @PostMapping("/organization-units")
    public ResponseEntity<Object> registerOrganizationUnit(@RequestBody @Valid final RegisterOrganizationRequest request) {
        return respond(partyService.register(new RegisterOrganizationUnit(new LegalName(request.name()))), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable final PartyId id) {
        return queryService.findById(id)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> problem(HttpStatus.NOT_FOUND, "No party found with id " + id.asString(), "PartyNotFound"));
    }

    @GetMapping
    public List<PartySummary> search(
            @RequestParam(required = false) final String type,
            @RequestParam(required = false) final String role,
            @RequestParam(required = false) final String identifier,
            @RequestParam(required = false) final String name) {
        return queryService.search(new PartySearchCriteria(type, role, identifier, name));
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<Object> assignRole(@PathVariable final PartyId id,
            @RequestBody @Valid final AssignRoleRequest request) {
        return respond(partyService.assignRole(new AssignRole(id, Role.named(request.name()))), HttpStatus.OK);
    }

    @PostMapping("/{id}/identifiers")
    public ResponseEntity<Object> registerIdentifier(@PathVariable final PartyId id,
            @RequestBody @Valid final RegisterIdentifierRequest request) {
        final var command = new RegisterIdentifier(id, toIdentifier(request));
        return respond(partyService.registerIdentifier(command), HttpStatus.OK);
    }

    private ResponseEntity<Object> respond(final Result<PartyError, PartyId> result, final HttpStatus successStatus) {
        return result.fold(this::failure, id -> success(id, successStatus));
    }

    private ResponseEntity<Object> success(final PartyId id, final HttpStatus status) {
        return queryService.findById(id)
                .<ResponseEntity<Object>>map(summary -> ResponseEntity.status(status).body(summary))
                .orElseGet(() -> problem(HttpStatus.NOT_FOUND, "No party found with id " + id.asString(), "PartyNotFound"));
    }

    private ResponseEntity<Object> failure(final PartyError error) {
        return problem(statusFor(error), detailFor(error), error.getClass().getSimpleName());
    }

    private static ResponseEntity<Object> problem(final HttpStatus status, final String detail, final String code) {
        final var body = ProblemDetail.forStatusAndDetail(status, detail);
        body.setProperty("code", code);
        return ResponseEntity.status(status).<Object>body(body);
    }

    private static HttpStatus statusFor(final PartyError error) {
        return switch (error) {
            case PartyError.PartyNotFound _ -> HttpStatus.NOT_FOUND;
            case PartyError.IdentifierNotEligible _ -> HttpStatus.UNPROCESSABLE_CONTENT;
            case PartyError.RoleAlreadyHeld _, PartyError.RoleNotHeld _,
                    PartyError.IdentifierAlreadyHeld _, PartyError.IdentifierNotHeld _ -> HttpStatus.CONFLICT;
        };
    }

    private static String detailFor(final PartyError error) {
        return switch (error) {
            case PartyError.PartyNotFound notFound -> "No party found with id " + notFound.id().asString();
            case PartyError.IdentifierNotEligible notEligible ->
                    "Identifier kind " + notEligible.kind() + " is not eligible for this party type";
            case PartyError.RoleAlreadyHeld _ -> "Party already holds the requested role";
            case PartyError.RoleNotHeld _ -> "Party does not hold the requested role";
            case PartyError.IdentifierAlreadyHeld _ -> "Party already holds the requested identifier";
            case PartyError.IdentifierNotHeld _ -> "Party does not hold the requested identifier";
        };
    }

    private static OfficialIdentifier toIdentifier(final RegisterIdentifierRequest request) {
        return switch (request.kind().toUpperCase(ROOT)) {
            case "TAX" -> new TaxIdentificationNumber(request.value());
            case "PASSPORT" -> new PassportNumber(request.value());
            case "NATIONAL" -> new NationalIdentificationNumber(request.value());
            default -> throw new IllegalArgumentException("Unknown identifier kind: " + request.kind());
        };
    }
}
