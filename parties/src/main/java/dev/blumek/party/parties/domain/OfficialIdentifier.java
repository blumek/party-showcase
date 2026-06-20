package dev.blumek.party.parties.domain;

public sealed interface OfficialIdentifier
        permits TaxIdentificationNumber, PassportNumber, NationalIdentificationNumber {

    IdentifierKind kind();

    String value();
}
