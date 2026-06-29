package dev.blumek.party.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.blumek.party.relationships.application.RelationshipQueryService;
import dev.blumek.party.relationships.application.RelationshipRepository;
import dev.blumek.party.relationships.application.RelationshipService;
import dev.blumek.party.shared.DomainEventPublisher;

@Configuration
class RelationshipsConfiguration {

    @Bean
    RelationshipService relationshipService(final RelationshipRepository repository,
            final DomainEventPublisher publisher) {
        return new RelationshipService(repository, publisher);
    }

    @Bean
    RelationshipQueryService relationshipQueryService(final RelationshipRepository repository) {
        return new RelationshipQueryService(repository);
    }
}
