package dev.blumek.party.addresses.domain;

public sealed interface AddressError {

    record AddressNotFound(AddressId id) implements AddressError {
    }

    record KindMismatch(AddressId id, ContactKind existing, ContactKind attempted) implements AddressError {
    }

    record DuplicateContact(ContactPoint contact) implements AddressError {
    }

    record OverlappingValidity(AddressPurpose purpose) implements AddressError {
    }
}
