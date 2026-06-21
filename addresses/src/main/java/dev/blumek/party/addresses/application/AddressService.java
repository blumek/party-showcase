package dev.blumek.party.addresses.application;

import java.util.Set;

import org.springframework.stereotype.Service;

import dev.blumek.party.addresses.domain.Address;
import dev.blumek.party.addresses.domain.AddressBook;
import dev.blumek.party.addresses.domain.AddressError;
import dev.blumek.party.addresses.domain.AddressId;
import dev.blumek.party.addresses.domain.AddressPurpose;
import dev.blumek.party.addresses.domain.ContactPoint;
import dev.blumek.party.addresses.domain.EmailAddress;
import dev.blumek.party.addresses.domain.PhoneNumber;
import dev.blumek.party.addresses.domain.PostalAddress;
import dev.blumek.party.addresses.domain.PostalCode;
import dev.blumek.party.addresses.domain.ValidityPeriod;
import dev.blumek.party.addresses.domain.WebsiteUrl;
import dev.blumek.party.shared.DomainEventPublisher;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;

@Service
public class AddressService {

    private final AddressRepository repository;
    private final DomainEventPublisher publisher;

    public AddressService(final AddressRepository repository, final DomainEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Result<AddressError, AddressId> recordEmail(final RecordEmailAddress command) {
        return record(command.owner(), contactPoint(command.addressId(), new EmailAddress(command.email()),
                command.purposes(), command.validity()));
    }

    public Result<AddressError, AddressId> recordPhone(final RecordPhoneNumber command) {
        return record(command.owner(), contactPoint(command.addressId(), new PhoneNumber(command.phone()),
                command.purposes(), command.validity()));
    }

    public Result<AddressError, AddressId> recordWebsite(final RecordWebsiteUrl command) {
        return record(command.owner(), contactPoint(command.addressId(), new WebsiteUrl(command.url()),
                command.purposes(), command.validity()));
    }

    public Result<AddressError, AddressId> recordPostal(final RecordPostalAddress command) {
        final var postal = new PostalAddress(command.line1(), command.line2(), command.city(),
                new PostalCode(command.postalCode()), command.country());
        return record(command.owner(), contactPoint(command.addressId(), postal, command.purposes(), command.validity()));
    }

    public Result<AddressError, AddressId> remove(final RemoveAddress command) {
        return repository.findByOwner(command.owner())
                .map(book -> book.remove(command.addressId()).onSuccess(id -> persist(book)))
                .orElseGet(() -> Result.failure(new AddressError.AddressNotFound(command.addressId())));
    }

    private Result<AddressError, AddressId> record(final OwnerId owner, final Address address) {
        final var book = repository.findByOwner(owner).orElseGet(() -> AddressBook.openFor(owner));
        return book.record(address).onSuccess(id -> persist(book));
    }

    private static Address contactPoint(final AddressId id, final ContactPoint contact,
            final Set<AddressPurpose> purposes, final ValidityPeriod validity) {
        return new Address(id == null ? AddressId.random() : id, contact, purposes, validity);
    }

    private void persist(final AddressBook book) {
        repository.save(book);
        publisher.publishAll(book.domainEvents());
        book.clearDomainEvents();
    }
}
