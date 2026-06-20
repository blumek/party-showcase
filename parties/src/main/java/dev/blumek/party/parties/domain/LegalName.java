package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.Guards;

public record LegalName(String value) {

    public LegalName {
        value = Guards.requireText(value, "Legal name cannot be blank").strip();
    }
}
