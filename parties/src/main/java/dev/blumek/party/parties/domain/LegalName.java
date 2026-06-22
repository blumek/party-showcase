package dev.blumek.party.parties.domain;

import static dev.blumek.party.shared.Guards.requireText;

public record LegalName(String value) {

    public LegalName {
        value = requireText(value, "Legal name cannot be blank").strip();
    }
}
