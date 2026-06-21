package dev.blumek.party.addresses.domain;

import java.util.Set;

import dev.blumek.party.shared.Guards;

public record Address(AddressId id, ContactPoint contact, Set<AddressPurpose> purposes, ValidityPeriod validity) {

    public Address {
        Guards.require(id != null, "Address requires an id");
        Guards.require(contact != null, "Address requires a contact point");
        Guards.require(purposes != null && !purposes.isEmpty(), "Address requires at least one purpose");
        Guards.require(validity != null, "Address requires a validity period");
        purposes = Set.copyOf(purposes);
    }

    public ContactKind kind() {
        return contact.kind();
    }

    public boolean sameKindAs(final Address other) {
        return kind() == other.kind();
    }

    public boolean differsFrom(final Address other) {
        return !contact.equals(other.contact) || !purposes.equals(other.purposes) || !validity.equals(other.validity);
    }
}
