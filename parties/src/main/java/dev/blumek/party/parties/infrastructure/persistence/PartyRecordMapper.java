package dev.blumek.party.parties.infrastructure.persistence;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import dev.blumek.party.parties.domain.Company;
import dev.blumek.party.parties.domain.IdentifierKind;
import dev.blumek.party.parties.domain.LegalName;
import dev.blumek.party.parties.domain.NationalIdentificationNumber;
import dev.blumek.party.parties.domain.OfficialIdentifier;
import dev.blumek.party.parties.domain.Organization;
import dev.blumek.party.parties.domain.OrganizationUnit;
import dev.blumek.party.parties.domain.Party;
import dev.blumek.party.parties.domain.PartyId;
import dev.blumek.party.parties.domain.PassportNumber;
import dev.blumek.party.parties.domain.Person;
import dev.blumek.party.parties.domain.PersonName;
import dev.blumek.party.parties.domain.PersonProfile;
import dev.blumek.party.parties.domain.Role;
import dev.blumek.party.parties.domain.TaxIdentificationNumber;

@Component
@Profile("jdbc")
class PartyRecordMapper {

    PartyRecord toRecord(final Party party) {
        return switch (party) {
            case Person person -> personRecord(person);
            case Company company -> organizationRecord(company, "COMPANY");
            case OrganizationUnit unit -> organizationRecord(unit, "ORGANIZATION_UNIT");
        };
    }

    Party toDomain(final PartyRecord entity) {
        final var id = new PartyId(entity.id());
        final var roles = roles(entity);
        final var identifiers = identifiers(entity);
        return switch (entity.type()) {
            case "PERSON" -> Person.rehydrate(id, profile(entity), roles, identifiers);
            case "COMPANY" -> Company.rehydrate(id, new LegalName(entity.legalName()), roles, identifiers);
            case "ORGANIZATION_UNIT" -> OrganizationUnit.rehydrate(id, new LegalName(entity.legalName()), roles, identifiers);
            default -> throw new IllegalStateException("Unknown party type: " + entity.type());
        };
    }

    private PartyRecord personRecord(final Person person) {
        final var profile = person.profile();
        return new PartyRecord(person.id().value(), "PERSON",
                profile.name().given(), profile.name().family(), profile.dateOfBirth(),
                null, roleRecords(person), identifierRecords(person));
    }

    private PartyRecord organizationRecord(final Organization organization, final String type) {
        return new PartyRecord(organization.id().value(), type,
                null, null, null, organization.name().value(),
                roleRecords(organization), identifierRecords(organization));
    }

    private Set<PartyRoleRecord> roleRecords(final Party party) {
        return party.roles().stream()
                .map(role -> new PartyRoleRecord(role.name().value()))
                .collect(Collectors.toSet());
    }

    private Set<PartyIdentifierRecord> identifierRecords(final Party party) {
        return party.identifiers().stream()
                .map(identifier -> new PartyIdentifierRecord(identifier.kind().name(), identifier.value()))
                .collect(Collectors.toSet());
    }

    private static PersonProfile profile(final PartyRecord entity) {
        return new PersonProfile(new PersonName(entity.givenName(), entity.familyName()), entity.dateOfBirth());
    }

    private Set<Role> roles(final PartyRecord entity) {
        return entity.roles().stream()
                .map(role -> Role.named(role.name()))
                .collect(Collectors.toSet());
    }

    private Set<OfficialIdentifier> identifiers(final PartyRecord entity) {
        return entity.identifiers().stream()
                .map(identifier -> identifierOf(identifier.kind(), identifier.value()))
                .collect(Collectors.toSet());
    }

    private static OfficialIdentifier identifierOf(final String kind, final String value) {
        return switch (IdentifierKind.valueOf(kind)) {
            case TAX -> new TaxIdentificationNumber(value);
            case PASSPORT -> new PassportNumber(value);
            case NATIONAL -> new NationalIdentificationNumber(value);
        };
    }
}
