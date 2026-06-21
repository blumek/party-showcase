package dev.blumek.party.addresses.application;

import java.util.Optional;
import java.util.Set;

import dev.blumek.party.addresses.domain.Address;
import dev.blumek.party.addresses.domain.AddressBook;
import dev.blumek.party.addresses.domain.AddressError;
import dev.blumek.party.addresses.domain.AddressId;
import dev.blumek.party.addresses.domain.AddressPurpose;
import dev.blumek.party.addresses.domain.EmailAddress;
import dev.blumek.party.addresses.domain.ValidityPeriod;
import dev.blumek.party.shared.DomainEventPublisher;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddressServiceTest {

    private final AddressRepository repository = mock(AddressRepository.class);
    private final DomainEventPublisher publisher = mock(DomainEventPublisher.class);
    private final AddressService service = new AddressService(repository, publisher);

    private final OwnerId owner = OwnerId.random();

    @Test
    void recordingAnEmailForANewOwnerPersistsTheBook() {
        givenNoBookFor(owner);

        service.recordEmail(recordEmail("ada@example.com"));

        thenABookWasSaved();
    }

    private void givenNoBookFor(final OwnerId owner) {
        when(repository.findByOwner(owner)).thenReturn(Optional.empty());
    }

    private RecordEmailAddress recordEmail(final String email) {
        return new RecordEmailAddress(owner, null, Set.of(AddressPurpose.NOTIFICATION), ValidityPeriod.always(), email);
    }

    private void thenABookWasSaved() {
        verify(repository).save(any(AddressBook.class));
    }

    @Test
    void recordingAnEmailReturnsTheNewAddressId() {
        givenNoBookFor(owner);

        var actualResult = service.recordEmail(recordEmail("ada@example.com"));

        thenSucceeded(actualResult);
    }

    private void thenSucceeded(final Result<AddressError, AddressId> result) {
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void recordingAnEmailPublishesTheRaisedEvents() {
        givenNoBookFor(owner);

        service.recordEmail(recordEmail("ada@example.com"));

        thenEventsWerePublished();
    }

    private void thenEventsWerePublished() {
        verify(publisher).publishAll(any());
    }

    @Test
    void aRejectedRecordIsNotPersisted() {
        givenBookHolding(givenStoredEmail("ada@example.com"));

        service.recordEmail(recordEmail("ada@example.com"));

        thenNothingWasSaved();
    }

    private Address givenStoredEmail(final String email) {
        return new Address(AddressId.random(), new EmailAddress(email), Set.of(AddressPurpose.NOTIFICATION),
                ValidityPeriod.always());
    }

    private void givenBookHolding(final Address address) {
        var book = AddressBook.openFor(owner);
        book.record(address);
        book.clearDomainEvents();
        when(repository.findByOwner(owner)).thenReturn(Optional.of(book));
    }

    private void thenNothingWasSaved() {
        verify(repository, never()).save(any());
    }

    @Test
    void aRejectedRecordIsNotPublished() {
        givenBookHolding(givenStoredEmail("ada@example.com"));

        service.recordEmail(recordEmail("ada@example.com"));

        thenNothingWasPublished();
    }

    private void thenNothingWasPublished() {
        verify(publisher, never()).publishAll(any());
    }

    @Test
    void removingFromAnUnknownOwnerReturnsAddressNotFound() {
        givenNoBookFor(owner);

        var actualResult = service.remove(new RemoveAddress(owner, AddressId.random()));

        thenFailedWith(actualResult, AddressError.AddressNotFound.class);
    }

    private void thenFailedWith(final Result<AddressError, AddressId> result, final Class<? extends AddressError> expected) {
        final AddressError actualError = result.fold(error -> error, id -> null);
        assertThat(actualError).isInstanceOf(expected);
    }

    @Test
    void removingAKnownAddressPersistsTheBook() {
        var stored = givenStoredEmail("ada@example.com");
        givenBookHolding(stored);

        service.remove(new RemoveAddress(owner, stored.id()));

        thenABookWasSaved();
    }
}
