package dev.blumek.party.parties.domain;

import java.time.LocalDate;

import dev.blumek.party.shared.Result;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdentifierEligibilityTest {

    @Test
    void aPersonAcceptsANationalIdentifier() {
        var person = givenPerson();

        var actualResult = person.registerIdentifier(nationalId());

        thenSucceeded(actualResult);
    }

    private Person givenPerson() {
        return Person.register(new PersonProfile(new PersonName("Ada", "Lovelace"), LocalDate.of(1815, 12, 10)));
    }

    private OfficialIdentifier nationalId() {
        return new NationalIdentificationNumber("44051401458");
    }

    private void thenSucceeded(final Result<PartyError, Party> result) {
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void aPersonAcceptsAPassport() {
        var person = givenPerson();

        var actualResult = person.registerIdentifier(passport());

        thenSucceeded(actualResult);
    }

    private OfficialIdentifier passport() {
        return new PassportNumber("AB1234567");
    }

    @Test
    void aPersonRejectsATaxIdentifier() {
        var person = givenPerson();

        var actualResult = person.registerIdentifier(taxId());

        thenNotEligible(actualResult);
    }

    private OfficialIdentifier taxId() {
        return new TaxIdentificationNumber("1234567890");
    }

    private void thenNotEligible(final Result<PartyError, Party> result) {
        final PartyError actualError = result.fold(error -> error, party -> null);
        assertThat(actualError).isInstanceOf(PartyError.IdentifierNotEligible.class);
    }

    @Test
    void aCompanyAcceptsATaxIdentifier() {
        var company = givenCompany();

        var actualResult = company.registerIdentifier(taxId());

        thenSucceeded(actualResult);
    }

    private Company givenCompany() {
        return Company.register(new LegalName("Acme Industries"));
    }

    @Test
    void aCompanyRejectsAPassport() {
        var company = givenCompany();

        var actualResult = company.registerIdentifier(passport());

        thenNotEligible(actualResult);
    }

    @Test
    void anOrganizationUnitAcceptsATaxIdentifier() {
        var unit = givenUnit();

        var actualResult = unit.registerIdentifier(taxId());

        thenSucceeded(actualResult);
    }

    private OrganizationUnit givenUnit() {
        return OrganizationUnit.register(new LegalName("Research Division"));
    }

    @Test
    void anOrganizationUnitRejectsANationalIdentifier() {
        var unit = givenUnit();

        var actualResult = unit.registerIdentifier(nationalId());

        thenNotEligible(actualResult);
    }
}
