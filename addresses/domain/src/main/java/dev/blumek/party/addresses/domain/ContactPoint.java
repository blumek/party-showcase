package dev.blumek.party.addresses.domain;

public sealed interface ContactPoint permits EmailAddress, PhoneNumber, WebsiteUrl, PostalAddress {

    ContactKind kind();
}
