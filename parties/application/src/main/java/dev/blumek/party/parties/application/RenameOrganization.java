package dev.blumek.party.parties.application;

import dev.blumek.party.parties.domain.LegalName;
import dev.blumek.party.parties.domain.PartyId;

public record RenameOrganization(PartyId partyId, LegalName name) {
}
