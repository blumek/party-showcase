package dev.blumek.party.parties.application;

import java.util.List;

public interface PartyFinder {

    List<PartySummary> search(PartySearchCriteria criteria);
}
