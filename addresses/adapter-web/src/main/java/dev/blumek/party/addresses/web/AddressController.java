package dev.blumek.party.addresses.web;

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
import org.springframework.web.bind.annotation.RestController;

import dev.blumek.party.addresses.application.AddressQueryService;
import dev.blumek.party.addresses.application.AddressService;
import dev.blumek.party.addresses.application.AddressSummary;
import dev.blumek.party.addresses.application.RecordEmailAddress;
import dev.blumek.party.addresses.application.RecordPhoneNumber;
import dev.blumek.party.addresses.application.RecordPostalAddress;
import dev.blumek.party.addresses.application.RecordWebsiteUrl;
import dev.blumek.party.addresses.application.RemoveAddress;
import dev.blumek.party.addresses.domain.AddressError;
import dev.blumek.party.addresses.domain.AddressId;
import dev.blumek.party.addresses.domain.ValidityPeriod;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;

@RestController
@RequestMapping("/parties/{partyId}/addresses")
class AddressController {

    private final AddressService addressService;
    private final AddressQueryService queryService;

    AddressController(final AddressService addressService, final AddressQueryService queryService) {
        this.addressService = addressService;
        this.queryService = queryService;
    }

    @PostMapping("/email")
    public ResponseEntity<Object> recordEmail(@PathVariable("partyId") final OwnerId owner,
            @RequestBody @Valid final RecordEmailRequest request) {
        final var command = new RecordEmailAddress(owner, addressId(request.addressId()), request.purposes(),
                validity(request.validFrom(), request.validTo()), request.email());
        return respond(owner, addressService.recordEmail(command));
    }

    @PostMapping("/phone")
    public ResponseEntity<Object> recordPhone(@PathVariable("partyId") final OwnerId owner,
            @RequestBody @Valid final RecordPhoneRequest request) {
        final var command = new RecordPhoneNumber(owner, addressId(request.addressId()), request.purposes(),
                validity(request.validFrom(), request.validTo()), request.phone());
        return respond(owner, addressService.recordPhone(command));
    }

    @PostMapping("/web")
    public ResponseEntity<Object> recordWeb(@PathVariable("partyId") final OwnerId owner,
            @RequestBody @Valid final RecordWebRequest request) {
        final var command = new RecordWebsiteUrl(owner, addressId(request.addressId()), request.purposes(),
                validity(request.validFrom(), request.validTo()), request.url());
        return respond(owner, addressService.recordWebsite(command));
    }

    @PostMapping("/postal")
    public ResponseEntity<Object> recordPostal(@PathVariable("partyId") final OwnerId owner,
            @RequestBody @Valid final RecordPostalRequest request) {
        final var command = new RecordPostalAddress(owner, addressId(request.addressId()), request.purposes(),
                validity(request.validFrom(), request.validTo()), request.line1(), request.line2(), request.city(),
                request.postalCode(), request.country());
        return respond(owner, addressService.recordPostal(command));
    }

    @GetMapping
    public List<AddressSummary> findAll(@PathVariable("partyId") final OwnerId owner) {
        return queryService.findByOwner(owner);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<Object> findById(@PathVariable("partyId") final OwnerId owner,
            @PathVariable final AddressId addressId) {
        return queryService.findById(owner, addressId)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> problem(HttpStatus.NOT_FOUND, "No address found with the given id", "AddressNotFound"));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Object> remove(@PathVariable("partyId") final OwnerId owner,
            @PathVariable final AddressId addressId) {
        final var command = new RemoveAddress(owner, addressId);
        return addressService.remove(command)
                .fold(this::failure, id -> ResponseEntity.noContent().build());
    }

    private ResponseEntity<Object> respond(final OwnerId owner, final Result<AddressError, AddressId> result) {
        return result.fold(this::failure, id -> success(owner, id));
    }

    private ResponseEntity<Object> success(final OwnerId owner, final AddressId id) {
        return queryService.findById(owner, id)
                .<ResponseEntity<Object>>map(summary -> ResponseEntity.status(HttpStatus.CREATED).body(summary))
                .orElseGet(() -> problem(HttpStatus.NOT_FOUND, "No address found with the given id", "AddressNotFound"));
    }

    private ResponseEntity<Object> failure(final AddressError error) {
        return problem(statusFor(error), detailFor(error), error.getClass().getSimpleName());
    }

    private static ResponseEntity<Object> problem(final HttpStatus status, final String detail, final String code) {
        final var body = ProblemDetail.forStatusAndDetail(status, detail);
        body.setProperty("code", code);
        return ResponseEntity.status(status).<Object>body(body);
    }

    private static AddressId addressId(final String raw) {
        return raw == null ? null : AddressId.of(raw);
    }

    private static ValidityPeriod validity(final LocalDate from, final LocalDate to) {
        return new ValidityPeriod(from, to);
    }

    private static HttpStatus statusFor(final AddressError error) {
        return switch (error) {
            case AddressError.AddressNotFound _ -> HttpStatus.NOT_FOUND;
            case AddressError.KindMismatch _, AddressError.DuplicateContact _,
                    AddressError.OverlappingValidity _ -> HttpStatus.CONFLICT;
        };
    }

    private static String detailFor(final AddressError error) {
        return switch (error) {
            case AddressError.AddressNotFound _ -> "No address found with the given id";
            case AddressError.KindMismatch _ -> "Address kind does not match the existing contact";
            case AddressError.DuplicateContact _ -> "An identical contact already exists";
            case AddressError.OverlappingValidity _ -> "The validity period overlaps an existing address for this purpose";
        };
    }
}
