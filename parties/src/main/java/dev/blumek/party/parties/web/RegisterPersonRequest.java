package dev.blumek.party.parties.web;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record RegisterPersonRequest(
        @NotBlank String given,
        @NotBlank String family,
        @NotNull @PastOrPresent LocalDate dateOfBirth) {
}
