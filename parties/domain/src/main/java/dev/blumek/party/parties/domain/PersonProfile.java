package dev.blumek.party.parties.domain;

import java.time.LocalDate;

import static dev.blumek.party.shared.Guards.require;

public record PersonProfile(PersonName name, LocalDate dateOfBirth) {

    public PersonProfile {
        require(name != null, "Person profile requires a name");
        require(dateOfBirth != null, "Person profile requires a date of birth");
        require(dateOfBirth.isBefore(LocalDate.now()), "Date of birth must be in the past");
    }
}
