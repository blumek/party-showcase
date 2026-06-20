package dev.blumek.party.parties.application;

import dev.blumek.party.parties.domain.OfficialIdentifier;
import dev.blumek.party.parties.domain.PartyId;

public record WithdrawIdentifier(PartyId partyId, OfficialIdentifier identifier) {
}
