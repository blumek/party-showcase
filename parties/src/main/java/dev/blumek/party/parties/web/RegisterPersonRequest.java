package dev.blumek.party.parties.web;

import java.time.LocalDate;

public record RegisterPersonRequest(String given, String family, LocalDate dateOfBirth) {
}
