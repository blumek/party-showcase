package dev.blumek.party.parties.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationUnitTest {

    @Test
    void registersWithTheGivenName() {
        var name = new LegalName("Research Division");

        var actualUnit = OrganizationUnit.register(name);

        thenNameIs(actualUnit, name);
    }

    private void thenNameIs(final Organization organization, final LegalName expected) {
        assertThat(organization.name()).isEqualTo(expected);
    }

    @Test
    void renamingReplacesTheName() {
        var unit = givenUnit();
        var newName = new LegalName("Research and Development");

        unit.rename(newName);

        thenNameIs(unit, newName);
    }

    private Organization givenUnit() {
        var unit = OrganizationUnit.register(new LegalName("Research Division"));
        unit.clearDomainEvents();
        return unit;
    }
}
