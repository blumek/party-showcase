package dev.blumek.party.parties.domain;

import static dev.blumek.party.shared.Guards.requireText;

public record PersonName(String given, String family) {

    public PersonName {
        given = requireText(given, "Given name cannot be blank").strip();
        family = requireText(family, "Family name cannot be blank").strip();
    }
}
