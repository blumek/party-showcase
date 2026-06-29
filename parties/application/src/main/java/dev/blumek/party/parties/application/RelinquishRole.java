package dev.blumek.party.parties.application;

import dev.blumek.party.parties.domain.PartyId;
import dev.blumek.party.parties.domain.Role;

public record RelinquishRole(PartyId partyId, Role role) {
}
