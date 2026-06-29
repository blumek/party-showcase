package dev.blumek.party.parties.infrastructure.persistence;

import java.time.LocalDate;

import dev.blumek.party.parties.domain.Company;
import dev.blumek.party.parties.domain.LegalName;
import dev.blumek.party.parties.domain.NationalIdentificationNumber;
import dev.blumek.party.parties.domain.OrganizationUnit;
import dev.blumek.party.parties.domain.Party;
import dev.blumek.party.parties.domain.PartyId;
import dev.blumek.party.parties.domain.PassportNumber;
import dev.blumek.party.parties.domain.Person;
import dev.blumek.party.parties.domain.PersonName;
import dev.blumek.party.parties.domain.PersonProfile;
import dev.blumek.party.parties.domain.Role;
import dev.blumek.party.parties.domain.TaxIdentificationNumber;
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
@TestPropertySource(properties = "spring.flyway.locations=classpath:db/migration/party")
@Import({JdbcPartyRepository.class, PartyRecordMapper.class})
class JdbcPartyRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private JdbcPartyRepository repository;

    @Test
    void roundTripsAPersonWithRolesAndIdentifiers() {
        var person = givenPerson();

        var actual = savedAndReloaded(person);

        assertThat(actual).isInstanceOf(Person.class);
        assertThat(((Person) actual).profile().name().family()).isEqualTo("Lovelace");
        assertThat(actual.roles()).extracting(role -> role.name().value()).containsExactly("DEVELOPER");
        assertThat(actual.identifiers()).extracting(identifier -> identifier.value())
                .containsExactlyInAnyOrder("12345678901", "AB1234567");
    }

    @Test
    void roundTripsACompany() {
        var company = givenCompany();

        var actual = savedAndReloaded(company);

        assertThat(actual).isInstanceOf(Company.class);
        assertThat(((Company) actual).name().value()).isEqualTo("Acme Corp");
        assertThat(actual.identifiers()).extracting(identifier -> identifier.value()).containsExactly("1234567890");
    }

    @Test
    void roundTripsAnOrganizationUnit() {
        var unit = givenOrganizationUnit();

        var actual = savedAndReloaded(unit);

        assertThat(actual).isInstanceOf(OrganizationUnit.class);
        assertThat(((OrganizationUnit) actual).name().value()).isEqualTo("Research Lab");
    }

    @Test
    void findAllReturnsEverySavedParty() {
        repository.save(givenPerson());
        repository.save(givenCompany());

        var actual = repository.findAll();

        assertThat(actual).hasSize(2);
    }

    @Test
    void assignsTheInitialVersionOnInsert() {
        var person = givenPerson();

        var actual = savedAndReloaded(person);

        assertThat(actual.version().number()).isEqualTo(1L);
    }

    @Test
    void rejectsAStaleConcurrentSave() {
        var person = givenPerson();
        repository.save(person);
        var first = reloaded(person.id());
        var second = reloaded(person.id());
        repository.save(first);

        var actualThrown = catchThrowable(() -> repository.save(second));

        thenOptimisticLockingFailed(actualThrown);
    }

    private Person givenPerson() {
        var person = Person.register(new PersonProfile(new PersonName("Ada", "Lovelace"), LocalDate.of(1990, 1, 1)));
        person.assignRole(Role.named("DEVELOPER"));
        person.registerIdentifier(new NationalIdentificationNumber("12345678901"));
        person.registerIdentifier(new PassportNumber("AB1234567"));
        return person;
    }

    private Company givenCompany() {
        var company = Company.register(new LegalName("Acme Corp"));
        company.registerIdentifier(new TaxIdentificationNumber("1234567890"));
        return company;
    }

    private OrganizationUnit givenOrganizationUnit() {
        return OrganizationUnit.register(new LegalName("Research Lab"));
    }

    private Party savedAndReloaded(final Party party) {
        repository.save(party);
        return repository.findById(party.id()).orElseThrow();
    }

    private Party reloaded(final PartyId id) {
        return repository.findById(id).orElseThrow();
    }

    private void thenOptimisticLockingFailed(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(OptimisticLockingFailureException.class);
    }
}
