package dev.blumek.party.addresses.application;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import dev.blumek.party.addresses.domain.Address;
import dev.blumek.party.addresses.domain.AddressId;
import dev.blumek.party.addresses.domain.ContactPoint;
import dev.blumek.party.addresses.domain.EmailAddress;
import dev.blumek.party.addresses.domain.PhoneNumber;
import dev.blumek.party.addresses.domain.PostalAddress;
import dev.blumek.party.addresses.domain.WebsiteUrl;
import dev.blumek.party.shared.OwnerId;

@Service
public class AddressQueryService {

    private final AddressRepository repository;

    public AddressQueryService(final AddressRepository repository) {
        this.repository = repository;
    }

    public List<AddressSummary> findByOwner(final OwnerId owner) {
        return repository.findByOwner(owner)
                .map(book -> book.addresses().stream().map(AddressQueryService::summarise).toList())
                .orElseGet(List::of);
    }

    public Optional<AddressSummary> findById(final OwnerId owner, final AddressId id) {
        return repository.findByOwner(owner)
                .flatMap(book -> book.find(id))
                .map(AddressQueryService::summarise);
    }

    private static AddressSummary summarise(final Address address) {
        return new AddressSummary(
                address.id().asString(),
                address.kind().name(),
                renderValue(address.contact()),
                purposeNames(address),
                address.validity().from(),
                address.validity().to(),
                postalOf(address.contact()));
    }

    private static String renderValue(final ContactPoint contact) {
        return switch (contact) {
            case EmailAddress email -> email.value();
            case PhoneNumber phone -> phone.value();
            case WebsiteUrl url -> url.value();
            case PostalAddress postal -> renderPostal(postal);
        };
    }

    private static String renderPostal(final PostalAddress postal) {
        final var builder = new StringBuilder(postal.line1());
        if (postal.line2() != null && !postal.line2().isBlank()) {
            builder.append(", ").append(postal.line2());
        }
        return builder.append(", ").append(postal.city())
                .append(", ").append(postal.postalCode().value())
                .append(", ").append(postal.country())
                .toString();
    }

    private static AddressSummary.Postal postalOf(final ContactPoint contact) {
        if (contact instanceof PostalAddress postal) {
            return new AddressSummary.Postal(postal.line1(), postal.line2(), postal.city(),
                    postal.postalCode().value(), postal.country());
        }
        return null;
    }

    private static Set<String> purposeNames(final Address address) {
        return address.purposes().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}
