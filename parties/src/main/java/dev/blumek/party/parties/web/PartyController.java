package dev.blumek.party.parties.web;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.blumek.party.parties.application.AssignRole;
import dev.blumek.party.parties.application.PartyQueryService;
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

@RestController
@RequestMapping("/parties")
public class PartyController {

    private final PartyService partyService;
    private final PartyQueryService queryService;

    public PartyController(final PartyService partyService, final PartyQueryService queryService) {
        this.partyService = partyService;
        this.queryService = queryService;
    }

    @PostMapping("/people")
    public ResponseEntity<PartySummary> registerPerson(@RequestBody final RegisterPersonRequest request) {
        final var profile = new PersonProfile(new PersonName(request.given(), request.family()), request.dateOfBirth());
        return respond(partyService.register(new RegisterPerson(profile)), HttpStatus.CREATED);
    }

    @PostMapping("/companies")
    public ResponseEntity<PartySummary> registerCompany(@RequestBody final RegisterOrganizationRequest request) {
        return respond(partyService.register(new RegisterCompany(new LegalName(request.name()))), HttpStatus.CREATED);
    }

    @PostMapping("/organization-units")
    public ResponseEntity<PartySummary> registerOrganizationUnit(@RequestBody final RegisterOrganizationRequest request) {
        return respond(partyService.register(new RegisterOrganizationUnit(new LegalName(request.name()))), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartySummary> findById(@PathVariable final String id) {
        return queryService.findById(PartyId.of(id))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<PartySummary> findAll() {
        return queryService.findAll();
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<PartySummary> assignRole(@PathVariable final String id,
            @RequestBody final AssignRoleRequest request) {
        return respond(partyService.assignRole(new AssignRole(PartyId.of(id), Role.named(request.name()))), HttpStatus.OK);
    }

    @PostMapping("/{id}/identifiers")
    public ResponseEntity<PartySummary> registerIdentifier(@PathVariable final String id,
            @RequestBody final RegisterIdentifierRequest request) {
        final var command = new RegisterIdentifier(PartyId.of(id), toIdentifier(request));
        return respond(partyService.registerIdentifier(command), HttpStatus.OK);
    }

    private ResponseEntity<PartySummary> respond(final Result<PartyError, PartyId> result, final HttpStatus successStatus) {
        return result.fold(this::failure, id -> success(id, successStatus));
    }

    private ResponseEntity<PartySummary> success(final PartyId id, final HttpStatus status) {
        return queryService.findById(id)
                .map(summary -> ResponseEntity.status(status).body(summary))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<PartySummary> failure(final PartyError error) {
        return ResponseEntity.status(statusFor(error)).build();
    }

    private static HttpStatus statusFor(final PartyError error) {
        return switch (error) {
            case PartyError.PartyNotFound _ -> HttpStatus.NOT_FOUND;
            case PartyError.IdentifierNotEligible _ -> HttpStatus.UNPROCESSABLE_CONTENT;
            case PartyError.RoleAlreadyHeld _, PartyError.RoleNotHeld _,
                    PartyError.IdentifierAlreadyHeld _, PartyError.IdentifierNotHeld _ -> HttpStatus.CONFLICT;
        };
    }

    private static OfficialIdentifier toIdentifier(final RegisterIdentifierRequest request) {
        return switch (request.kind().toUpperCase(Locale.ROOT)) {
            case "TAX" -> new TaxIdentificationNumber(request.value());
            case "PASSPORT" -> new PassportNumber(request.value());
            case "NATIONAL" -> new NationalIdentificationNumber(request.value());
            default -> throw new IllegalArgumentException("Unknown identifier kind: " + request.kind());
        };
    }
}
