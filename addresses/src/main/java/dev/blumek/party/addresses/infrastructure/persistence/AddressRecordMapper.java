package dev.blumek.party.addresses.infrastructure.persistence;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import dev.blumek.party.addresses.domain.Address;
import dev.blumek.party.addresses.domain.AddressBook;
import dev.blumek.party.addresses.domain.AddressId;
import dev.blumek.party.addresses.domain.AddressPurpose;
import dev.blumek.party.addresses.domain.ContactKind;
import dev.blumek.party.addresses.domain.ContactPoint;
import dev.blumek.party.addresses.domain.EmailAddress;
import dev.blumek.party.addresses.domain.PhoneNumber;
import dev.blumek.party.addresses.domain.PostalAddress;
import dev.blumek.party.addresses.domain.PostalCode;
import dev.blumek.party.addresses.domain.ValidityPeriod;
import dev.blumek.party.addresses.domain.WebsiteUrl;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Version;

@Component
@Profile("jdbc")
class AddressRecordMapper {

    AddressBookRecord toRecord(final AddressBook book) {
        final var addresses = book.addresses().stream()
                .map(AddressRecordMapper::addressRecord)
                .collect(Collectors.toSet());
        return new AddressBookRecord(book.owner().value(), book.version().number(), addresses);
    }

    AddressBook toDomain(final AddressBookRecord entity) {
        final var addresses = entity.addresses().stream()
                .map(AddressRecordMapper::address)
                .toList();
        return AddressBook.rehydrate(new OwnerId(entity.ownerId()), addresses, new Version(entity.version()));
    }

    private static AddressRecord addressRecord(final Address address) {
        final var contact = address.contact();
        return new AddressRecord(address.id().value(), address.kind().name(),
                line1(contact), line2(contact), city(contact), postalCode(contact), country(contact),
                email(contact), phone(contact), websiteUrl(contact),
                address.validity().from(), address.validity().to(),
                purposeRecords(address));
    }

    private static Address address(final AddressRecord entity) {
        return new Address(new AddressId(entity.id()), contactPoint(entity), purposes(entity),
                new ValidityPeriod(entity.validFrom(), entity.validTo()));
    }

    private static ContactPoint contactPoint(final AddressRecord entity) {
        return switch (ContactKind.valueOf(entity.kind())) {
            case POSTAL -> new PostalAddress(entity.line1(), entity.line2(), entity.city(),
                    new PostalCode(entity.postalCode()), entity.country());
            case EMAIL -> new EmailAddress(entity.email());
            case PHONE -> new PhoneNumber(entity.phone());
            case WEBSITE -> new WebsiteUrl(entity.websiteUrl());
        };
    }

    private static Set<AddressPurposeRecord> purposeRecords(final Address address) {
        return address.purposes().stream()
                .map(purpose -> new AddressPurposeRecord(purpose.name()))
                .collect(Collectors.toSet());
    }

    private static Set<AddressPurpose> purposes(final AddressRecord entity) {
        return entity.purposes().stream()
                .map(purpose -> AddressPurpose.valueOf(purpose.purpose()))
                .collect(Collectors.toSet());
    }

    private static String line1(final ContactPoint contact) {
        return contact instanceof PostalAddress postal ? postal.line1() : null;
    }

    private static String line2(final ContactPoint contact) {
        return contact instanceof PostalAddress postal ? postal.line2() : null;
    }

    private static String city(final ContactPoint contact) {
        return contact instanceof PostalAddress postal ? postal.city() : null;
    }

    private static String postalCode(final ContactPoint contact) {
        return contact instanceof PostalAddress postal ? postal.postalCode().value() : null;
    }

    private static String country(final ContactPoint contact) {
        return contact instanceof PostalAddress postal ? postal.country() : null;
    }

    private static String email(final ContactPoint contact) {
        return contact instanceof EmailAddress email ? email.value() : null;
    }

    private static String phone(final ContactPoint contact) {
        return contact instanceof PhoneNumber phone ? phone.value() : null;
    }

    private static String websiteUrl(final ContactPoint contact) {
        return contact instanceof WebsiteUrl website ? website.value() : null;
    }
}
