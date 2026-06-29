package dev.blumek.party.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.blumek.party.parties.application.PartyFinder;
import dev.blumek.party.parties.application.PartyQueryService;
import dev.blumek.party.parties.application.PartyRepository;
import dev.blumek.party.parties.application.PartyService;
import dev.blumek.party.shared.DomainEventPublisher;

@Configuration
class PartiesConfiguration {

    @Bean
    PartyService partyService(final PartyRepository repository, final DomainEventPublisher publisher) {
        return new PartyService(repository, publisher);
    }

    @Bean
    PartyQueryService partyQueryService(final PartyRepository repository, final PartyFinder finder) {
        return new PartyQueryService(repository, finder);
    }
}
