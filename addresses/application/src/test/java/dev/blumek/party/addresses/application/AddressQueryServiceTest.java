package dev.blumek.party.addresses.application;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import dev.blumek.party.addresses.domain.Address;
import dev.blumek.party.addresses.domain.AddressBook;
import dev.blumek.party.addresses.domain.AddressId;
import dev.blumek.party.addresses.domain.AddressPurpose;
import dev.blumek.party.addresses.domain.EmailAddress;
import dev.blumek.party.addresses.domain.PostalAddress;
import dev.blumek.party.addresses.domain.PostalCode;
import dev.blumek.party.addresses.domain.ValidityPeriod;
import dev.blumek.party.shared.OwnerId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddressQueryServiceTest {

    private final AddressRepository repository = mock(AddressRepository.class);
    private final AddressQueryService service = new AddressQueryService(repository);

    private final OwnerId owner = OwnerId.random();

    @Test
    void summarisesAnEmailWithItsKindAndValue() {
        var stored = givenStored(emailAddress("ada@example.com"));

        var actualSummary = service.findById(owner, stored.id()).orElseThrow();

        thenHeaderIs(actualSummary, "EMAIL", "ada@example.com");
    }

    private Address emailAddress(final String value) {
        return new Address(AddressId.random(), new EmailAddress(value), Set.of(AddressPurpose.NOTIFICATION),
                ValidityPeriod.from(LocalDate.of(2026, 1, 1)));
    }

    private Address givenStored(final Address... addresses) {
        var book = AddressBook.openFor(owner);
        for (final var address : addresses) {
            book.record(address);
        }
        when(repository.findByOwner(owner)).thenReturn(Optional.of(book));
        return addresses[0];
    }

    private void thenHeaderIs(final AddressSummary summary, final String kind, final String value) {
        assertThat(summary.kind()).isEqualTo(kind);
        assertThat(summary.value()).isEqualTo(value);
    }

    @Test
    void includesPurposesAndValidityBounds() {
        var stored = givenStored(emailAddress("ada@example.com"));

        var actualSummary = service.findById(owner, stored.id()).orElseThrow();

        thenPurposesAndBounds(actualSummary);
    }

    private void thenPurposesAndBounds(final AddressSummary summary) {
        assertThat(summary.purposes()).containsExactly("NOTIFICATION");
        assertThat(summary.validFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(summary.validTo()).isNull();
    }

    @Test
    void summarisesAPostalAddressWithItsBreakdown() {
        var stored = givenStored(postalAddress());

        var actualSummary = service.findById(owner, stored.id()).orElseThrow();

        thenPostalBreakdownIsPresent(actualSummary);
    }

    private Address postalAddress() {
        var postal = new PostalAddress("221B Baker Street", null, "London", new PostalCode("NW1 6XE"), "GB");
        return new Address(AddressId.random(), postal, Set.of(AddressPurpose.RESIDENCE), ValidityPeriod.always());
    }

    private void thenPostalBreakdownIsPresent(final AddressSummary summary) {
        assertThat(summary.kind()).isEqualTo("POSTAL");
        assertThat(summary.postal().city()).isEqualTo("London");
        assertThat(summary.postal().country()).isEqualTo("GB");
    }

    @Test
    void leavesPostalNullForANonPostalAddress() {
        var stored = givenStored(emailAddress("ada@example.com"));

        var actualSummary = service.findById(owner, stored.id()).orElseThrow();

        thenPostalIsAbsent(actualSummary);
    }

    private void thenPostalIsAbsent(final AddressSummary summary) {
        assertThat(summary.postal()).isNull();
    }

    @Test
    void returnsEmptyForAnUnknownAddress() {
        givenStored(emailAddress("ada@example.com"));

        var actualSummary = service.findById(owner, AddressId.random());

        thenAbsent(actualSummary);
    }

    private void thenAbsent(final Optional<AddressSummary> summary) {
        assertThat(summary).isEmpty();
    }

    @Test
    void listsEveryAddressForTheOwner() {
        givenStored(emailAddress("ada@example.com"), postalAddress());

        var actualSummaries = service.findByOwner(owner);

        thenSummaryCountIs(actualSummaries.size(), 2);
    }

    private void thenSummaryCountIs(final int actual, final int expected) {
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void returnsAnEmptyListForAnUnknownOwner() {
        when(repository.findByOwner(owner)).thenReturn(Optional.empty());

        var actualSummaries = service.findByOwner(owner);

        thenSummaryCountIs(actualSummaries.size(), 0);
    }
}
