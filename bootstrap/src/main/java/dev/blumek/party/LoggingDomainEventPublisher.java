package dev.blumek.party;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.DomainEventPublisher;

@Component
class LoggingDomainEventPublisher implements DomainEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingDomainEventPublisher.class);

    @Override
    public void publish(final DomainEvent event) {
        LOG.info("Domain event published: {}", event);
    }
}
