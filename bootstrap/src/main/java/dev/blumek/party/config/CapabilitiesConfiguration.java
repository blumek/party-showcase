package dev.blumek.party.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.blumek.party.capabilities.application.CapabilityQueryService;
import dev.blumek.party.capabilities.application.CapabilityRepository;
import dev.blumek.party.capabilities.application.CapabilityService;
import dev.blumek.party.shared.DomainEventPublisher;

@Configuration
class CapabilitiesConfiguration {

    @Bean
    CapabilityService capabilityService(final CapabilityRepository repository, final DomainEventPublisher publisher) {
        return new CapabilityService(repository, publisher);
    }

    @Bean
    CapabilityQueryService capabilityQueryService(final CapabilityRepository repository) {
        return new CapabilityQueryService(repository);
    }
}
