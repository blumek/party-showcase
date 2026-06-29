package dev.blumek.party.parties.application;

public record PartySearchCriteria(String type, String role, String identifier, String nameContains) {

    public static PartySearchCriteria any() {
        return new PartySearchCriteria(null, null, null, null);
    }
}
