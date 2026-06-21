package dev.blumek.party.addresses.domain;

import java.util.Set;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressBookTest {

    private final OwnerId owner = OwnerId.random();

    @Test
    void recordingANewAddressStoresIt() {
        var book = AddressBook.openFor(owner);
        var address = givenEmail("ada@example.com");

        book.record(address);

        thenBookContains(book, address);
    }

    private Address givenEmail(final String value) {
        return new Address(AddressId.random(), new EmailAddress(value), Set.of(AddressPurpose.NOTIFICATION),
                ValidityPeriod.always());
    }

    private void thenBookContains(final AddressBook book, final Address address) {
        assertThat(book.addresses()).contains(address);
    }

    @Test
    void recordingANewAddressRaisesAddressRecorded() {
        var book = AddressBook.openFor(owner);
        var address = givenEmail("ada@example.com");

        book.record(address);

        thenEventsAre(book, new AddressRecorded(owner, address.id(), ContactKind.EMAIL));
    }

    private void thenEventsAre(final AddressBook book, final DomainEvent... expected) {
        assertThat(book.domainEvents()).containsExactly(expected);
    }

    @Test
    void recordingANewAddressAdvancesTheVersion() {
        var book = AddressBook.openFor(owner);

        book.record(givenEmail("ada@example.com"));

        thenVersionNumberIs(book, 1);
    }

    private void thenVersionNumberIs(final AddressBook book, final long expected) {
        assertThat(book.version().number()).isEqualTo(expected);
    }

    @Test
    void revisingAnExistingAddressRaisesAddressRevised() {
        var book = givenBookHolding(givenEmail("ada@example.com"));
        var stored = book.addresses().getFirst();

        book.record(new Address(stored.id(), new EmailAddress("ada@elsewhere.com"), stored.purposes(), stored.validity()));

        thenEventsAre(book, new AddressRevised(owner, stored.id(), ContactKind.EMAIL));
    }

    private AddressBook givenBookHolding(final Address address) {
        var book = AddressBook.openFor(owner);
        book.record(address);
        book.clearDomainEvents();
        return book;
    }

    @Test
    void revisingWithIdenticalContentRaisesNoEvent() {
        var book = givenBookHolding(givenEmail("ada@example.com"));
        var stored = book.addresses().getFirst();

        book.record(new Address(stored.id(), stored.contact(), stored.purposes(), stored.validity()));

        thenNoEventsRaised(book);
    }

    private void thenNoEventsRaised(final AddressBook book) {
        assertThat(book.domainEvents()).isEmpty();
    }

    @Test
    void recordingADifferentKindUnderTheSameIdReturnsKindMismatch() {
        var book = givenBookHolding(givenEmail("ada@example.com"));
        var stored = book.addresses().getFirst();

        var actualResult = book.record(new Address(stored.id(), new PhoneNumber("020 7946 0958"),
                Set.of(AddressPurpose.NOTIFICATION), ValidityPeriod.always()));

        thenFailedWith(actualResult, AddressError.KindMismatch.class);
    }

    private void thenFailedWith(final Result<AddressError, AddressId> result, final Class<? extends AddressError> expected) {
        final AddressError actualError = result.fold(error -> error, id -> null);
        assertThat(actualError).isInstanceOf(expected);
    }

    @Test
    void recordingADuplicateContactReturnsDuplicateContact() {
        var book = givenBookHolding(givenEmail("ada@example.com"));

        var actualResult = book.record(givenEmail("ada@example.com"));

        thenFailedWith(actualResult, AddressError.DuplicateContact.class);
    }

    @Test
    void removingAKnownAddressRaisesAddressWithdrawn() {
        var book = givenBookHolding(givenEmail("ada@example.com"));
        var stored = book.addresses().getFirst();

        book.remove(stored.id());

        thenEventsAre(book, new AddressWithdrawn(owner, stored.id(), ContactKind.EMAIL));
    }

    @Test
    void removingAnUnknownAddressReturnsAddressNotFound() {
        var book = AddressBook.openFor(owner);

        var actualResult = book.remove(AddressId.random());

        thenFailedWith(actualResult, AddressError.AddressNotFound.class);
    }
}
