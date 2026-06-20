package dev.blumek.party.parties.domain;

import java.time.LocalDate;

import dev.blumek.party.shared.Guards;

public record PersonProfile(PersonName name, LocalDate dateOfBirth) {

    public PersonProfile {
        Guards.require(name != null, "Person profile requires a name");
        Guards.require(dateOfBirth != null, "Person profile requires a date of birth");
        Guards.require(dateOfBirth.isBefore(LocalDate.now()), "Date of birth must be in the past");
    }
}
