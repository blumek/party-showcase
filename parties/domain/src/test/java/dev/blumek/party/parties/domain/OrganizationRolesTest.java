package dev.blumek.party.parties.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationRolesTest {

    @Test
    void companiesCanHoldRoles() {
        var company = Company.register(new LegalName("Acme Industries"));

        company.assignRole(Role.named("Supplier"));

        thenRolesContain(company, Role.named("Supplier"));
    }

    private void thenRolesContain(final Party party, final Role role) {
        assertThat(party.roles()).contains(role);
    }

    @Test
    void organizationUnitsCanHoldRoles() {
        var unit = OrganizationUnit.register(new LegalName("Research Division"));

        unit.assignRole(Role.named("CostCentre"));

        thenRolesContain(unit, Role.named("CostCentre"));
    }
}
