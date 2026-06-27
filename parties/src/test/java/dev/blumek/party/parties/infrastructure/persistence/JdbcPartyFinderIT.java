package dev.blumek.party.parties.infrastructure.persistence;

import java.time.LocalDate;

import dev.blumek.party.parties.application.PartySearchCriteria;
import dev.blumek.party.parties.domain.Company;
import dev.blumek.party.parties.domain.LegalName;
import dev.blumek.party.parties.domain.NationalIdentificationNumber;
import dev.blumek.party.parties.domain.Person;
import dev.blumek.party.parties.domain.PersonName;
import dev.blumek.party.parties.domain.PersonProfile;
import dev.blumek.party.parties.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = NONE)
@Testcontainers
@ActiveProfiles("jdbc")
@TestPropertySource(properties = "spring.flyway.locations=classpath:db/migration/party")
@Import({JdbcPartyRepository.class, PartyRecordMapper.class, JdbcPartyFinder.class})
class JdbcPartyFinderIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private JdbcPartyRepository repository;

    @Autowired
    private JdbcPartyFinder finder;

    @BeforeEach
    void seed() {
        repository.save(person());
        repository.save(Company.register(new LegalName("Acme Industries")));
    }

    @Test
    void findsByRole() {
        var actual = finder.search(new PartySearchCriteria(null, "Customer", null, null));

        assertThat(actual).singleElement().extracting(summary -> summary.displayName()).isEqualTo("Ada Lovelace");
    }

    @Test
    void findsByType() {
        var actual = finder.search(new PartySearchCriteria("COMPANY", null, null, null));

        assertThat(actual).singleElement().extracting(summary -> summary.displayName()).isEqualTo("Acme Industries");
    }

    @Test
    void findsByNameFragment() {
        var actual = finder.search(new PartySearchCriteria(null, null, null, "industries"));

        assertThat(actual).singleElement().extracting(summary -> summary.kind()).isEqualTo("COMPANY");
    }

    @Test
    void findsByIdentifierValueWithRolesAndIdentifiersAttached() {
        var actual = finder.search(new PartySearchCriteria(null, null, "44051401458", null));

        assertThat(actual).singleElement().satisfies(summary -> {
            assertThat(summary.roles()).containsExactly("Customer");
            assertThat(summary.identifiers())
                    .extracting(identifier -> identifier.value()).containsExactly("44051401458");
        });
    }

    @Test
    void returnsEveryPartyForEmptyCriteria() {
        var actual = finder.search(PartySearchCriteria.any());

        assertThat(actual).hasSize(2);
    }

    private Person person() {
        var person = Person.register(new PersonProfile(new PersonName("Ada", "Lovelace"), LocalDate.of(1815, 12, 10)));
        person.assignRole(Role.named("Customer"));
        person.registerIdentifier(new NationalIdentificationNumber("44051401458"));
        return person;
    }
}
