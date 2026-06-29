package dev.blumek.party.parties.application;

import java.util.Set;
import java.util.stream.Collectors;

import dev.blumek.party.parties.domain.Company;
import dev.blumek.party.parties.domain.Organization;
import dev.blumek.party.parties.domain.OrganizationUnit;
import dev.blumek.party.parties.domain.Party;
import dev.blumek.party.parties.domain.Person;

public final class PartySummaries {

    private PartySummaries() {
    }

    public static PartySummary of(final Party party) {
        return new PartySummary(party.id().asString(), kindOf(party), displayNameOf(party),
                roleNames(party), identifierSummaries(party));
    }

    private static String kindOf(final Party party) {
        return switch (party) {
            case Person _ -> "PERSON";
            case Company _ -> "COMPANY";
            case OrganizationUnit _ -> "ORGANIZATION_UNIT";
        };
    }

    private static String displayNameOf(final Party party) {
        return switch (party) {
            case Person person -> person.profile().name().given() + " " + person.profile().name().family();
            case Organization organization -> organization.name().value();
        };
    }

    private static Set<String> roleNames(final Party party) {
        return party.roles().stream()
                .map(role -> role.name().value())
                .collect(Collectors.toSet());
    }

    private static Set<PartySummary.IdentifierSummary> identifierSummaries(final Party party) {
        return party.identifiers().stream()
                .map(identifier -> new PartySummary.IdentifierSummary(identifier.kind().name(), identifier.value()))
                .collect(Collectors.toSet());
    }
}
