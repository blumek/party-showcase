package dev.blumek.party;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import dev.blumek.party.shared.DomainEvent;

@Component
class DomainEventLogger {

    private static final Logger LOG = LoggerFactory.getLogger(DomainEventLogger.class);

    @EventListener
    void on(final DomainEvent event) {
        LOG.info("Domain event published: {}", event);
    }
}
