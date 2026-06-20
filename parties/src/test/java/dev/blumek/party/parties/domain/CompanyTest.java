package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyTest {

    @Test
    void registersWithTheGivenName() {
        var name = new LegalName("Acme Industries");

        var actualCompany = Company.register(name);

        thenNameIs(actualCompany, name);
    }

    private void thenNameIs(final Organization organization, final LegalName expected) {
        assertThat(organization.name()).isEqualTo(expected);
    }

    @Test
    void registrationRaisesPartyRegistered() {
        var actualCompany = Company.register(new LegalName("Acme Industries"));

        thenEventsAre(actualCompany, new PartyRegistered(actualCompany.id()));
    }

    private void thenEventsAre(final Organization organization, final DomainEvent... expected) {
        assertThat(organization.domainEvents()).containsExactly(expected);
    }

    @Test
    void renamingReplacesTheName() {
        var company = givenCompany();
        var newName = new LegalName("Acme Worldwide");

        company.rename(newName);

        thenNameIs(company, newName);
    }

    private Organization givenCompany() {
        var company = Company.register(new LegalName("Acme Industries"));
        company.clearDomainEvents();
        return company;
    }

    @Test
    void renamingRaisesOrganizationRenamed() {
        var company = givenCompany();
        var newName = new LegalName("Acme Worldwide");

        company.rename(newName);

        thenEventsAre(company, new OrganizationRenamed(company.id(), newName));
    }
}
