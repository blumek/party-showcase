package dev.blumek.party.parties.domain;

import dev.blumek.party.shared.Guards;

public record PersonName(String given, String family) {

    public PersonName {
        given = Guards.requireText(given, "Given name cannot be blank").strip();
        family = Guards.requireText(family, "Family name cannot be blank").strip();
    }
}
