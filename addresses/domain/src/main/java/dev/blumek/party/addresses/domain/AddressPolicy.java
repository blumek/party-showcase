package dev.blumek.party.addresses.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@FunctionalInterface
public interface AddressPolicy {

    AddressPolicy DEFAULT = combined(noDuplicateContacts(), noOverlappingValidityForPurpose());

    Optional<AddressError> check(Address candidate, Collection<Address> existing);

    static AddressPolicy permitAll() {
        return (candidate, existing) -> Optional.empty();
    }

    static AddressPolicy noDuplicateContacts() {
        return (candidate, existing) -> existing.stream()
                .anyMatch(address -> address.contact().equals(candidate.contact()))
                ? Optional.of(new AddressError.DuplicateContact(candidate.contact()))
                : Optional.empty();
    }

    static AddressPolicy noOverlappingValidityForPurpose() {
        return (candidate, existing) -> candidate.purposes().stream()
                .filter(purpose -> overlapsExistingFor(purpose, candidate, existing))
                .findFirst()
                .map(AddressError.OverlappingValidity::new);
    }

    static AddressPolicy combined(final AddressPolicy... policies) {
        return (candidate, existing) -> Arrays.stream(policies)
                .map(policy -> policy.check(candidate, existing))
                .flatMap(Optional::stream)
                .findFirst();
    }

    private static boolean overlapsExistingFor(final AddressPurpose purpose, final Address candidate,
            final Collection<Address> existing) {
        return existing.stream()
                .filter(address -> address.purposes().contains(purpose))
                .anyMatch(address -> address.validity().overlaps(candidate.validity()));
    }
}
