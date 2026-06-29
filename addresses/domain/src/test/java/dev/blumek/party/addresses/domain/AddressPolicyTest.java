package dev.blumek.party.addresses.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressPolicyTest {

    @Test
    void noDuplicateContactsFlagsARepeatedContact() {
        var candidate = givenEmail("ada@example.com", AddressPurpose.NOTIFICATION, ValidityPeriod.always());

        var actualViolation = AddressPolicy.noDuplicateContacts().check(candidate, List.of(candidate));

        thenViolationIs(actualViolation.orElse(null), AddressError.DuplicateContact.class);
    }

    private Address givenEmail(final String value, final AddressPurpose purpose, final ValidityPeriod validity) {
        return new Address(AddressId.random(), new EmailAddress(value), Set.of(purpose), validity);
    }

    private void thenViolationIs(final AddressError actual, final Class<? extends AddressError> expected) {
        assertThat(actual).isInstanceOf(expected);
    }

    @Test
    void noDuplicateContactsPermitsADistinctContact() {
        var candidate = givenEmail("ada@example.com", AddressPurpose.NOTIFICATION, ValidityPeriod.always());
        var other = givenEmail("grace@example.com", AddressPurpose.NOTIFICATION, ValidityPeriod.always());

        var actualViolation = AddressPolicy.noDuplicateContacts().check(candidate, List.of(other));

        thenNoViolation(actualViolation.isEmpty());
    }

    private void thenNoViolation(final boolean actual) {
        assertThat(actual).isTrue();
    }

    @Test
    void noOverlappingValidityFlagsAnOverlapOnASharedPurpose() {
        var existing = givenEmail("a@example.com", AddressPurpose.BILLING,
                ValidityPeriod.between(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 7, 1)));
        var candidate = givenEmail("b@example.com", AddressPurpose.BILLING,
                ValidityPeriod.between(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 8, 1)));

        var actualViolation = AddressPolicy.noOverlappingValidityForPurpose().check(candidate, List.of(existing));

        thenViolationIs(actualViolation.orElse(null), AddressError.OverlappingValidity.class);
    }

    @Test
    void noOverlappingValidityPermitsADifferentPurpose() {
        var existing = givenEmail("a@example.com", AddressPurpose.BILLING, ValidityPeriod.always());
        var candidate = givenEmail("b@example.com", AddressPurpose.DELIVERY, ValidityPeriod.always());

        var actualViolation = AddressPolicy.noOverlappingValidityForPurpose().check(candidate, List.of(existing));

        thenNoViolation(actualViolation.isEmpty());
    }

    @Test
    void permitAllNeverFlagsAnything() {
        var candidate = givenEmail("ada@example.com", AddressPurpose.NOTIFICATION, ValidityPeriod.always());

        var actualViolation = AddressPolicy.permitAll().check(candidate, List.of(candidate));

        thenNoViolation(actualViolation.isEmpty());
    }

    @Test
    void combinedReturnsTheFirstViolation() {
        var candidate = givenEmail("ada@example.com", AddressPurpose.BILLING, ValidityPeriod.always());
        var policy = AddressPolicy.combined(AddressPolicy.noDuplicateContacts(),
                AddressPolicy.noOverlappingValidityForPurpose());

        var actualViolation = policy.check(candidate, List.of(candidate));

        thenViolationIs(actualViolation.orElse(null), AddressError.DuplicateContact.class);
    }
}
