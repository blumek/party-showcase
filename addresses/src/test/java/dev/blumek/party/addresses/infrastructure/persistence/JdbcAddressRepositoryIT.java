package dev.blumek.party.addresses.infrastructure.persistence;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = NONE)
@Testcontainers
@ActiveProfiles("jdbc")
@TestPropertySource(properties = "spring.flyway.locations=classpath:db/migration/address")
@Import({JdbcAddressRepository.class, AddressRecordMapper.class})
class JdbcAddressRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private JdbcAddressRepository repository;

    private final OwnerId owner = OwnerId.random();

    @Test
    void roundTripsAnAddressBookWithItsContacts() {
        var book = givenBookWithPostalAndEmail();

        var actual = savedAndReloaded(book);

        assertThat(actual.addresses()).hasSize(2);
        assertThat(actual.addresses()).extracting(address -> address.kind().name())
                .containsExactlyInAnyOrder("POSTAL", "EMAIL");
    }

    @Test
    void preservesThePostalDetailsAndPurposes() {
        var book = givenBookWithPostalAndEmail();

        var actual = savedAndReloaded(book);

        var postal = (PostalAddress) actual.addresses().stream()
                .filter(address -> address.kind().name().equals("POSTAL"))
                .findFirst().orElseThrow().contact();
        assertThat(postal.city()).isEqualTo("Warsaw");
        assertThat(postal.postalCode().value()).isEqualTo("00-001");
    }

    @Test
    void assignsTheInitialVersionOnInsert() {
        var book = givenBookWithPostalAndEmail();

        var actual = savedAndReloaded(book);

        assertThat(actual.version().number()).isEqualTo(1L);
    }

    @Test
    void rejectsAStaleConcurrentSave() {
        repository.save(givenBookWithPostalAndEmail());
        var first = reloaded();
        var second = reloaded();
        repository.save(first);

        var actualThrown = catchThrowable(() -> repository.save(second));

        thenOptimisticLockingFailed(actualThrown);
    }

    private AddressBook givenBookWithPostalAndEmail() {
        var book = AddressBook.openFor(owner);
        book.record(new Address(AddressId.random(),
                new PostalAddress("1 Market", "Apt 4", "Warsaw", new PostalCode("00-001"), "PL"),
                Set.of(AddressPurpose.RESIDENCE), ValidityPeriod.always()));
        book.record(new Address(AddressId.random(),
                new EmailAddress("ada@example.com"),
                Set.of(AddressPurpose.NOTIFICATION), ValidityPeriod.always()));
        return book;
    }

    private AddressBook savedAndReloaded(final AddressBook book) {
        repository.save(book);
        return repository.findByOwner(book.owner()).orElseThrow();
    }

    private AddressBook reloaded() {
        return repository.findByOwner(owner).orElseThrow();
    }

    private void thenOptimisticLockingFailed(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(OptimisticLockingFailureException.class);
    }
}
