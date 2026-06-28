package dev.blumek.party;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.DomainEventPublisher;

@Component
class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher publisher;

    SpringDomainEventPublisher(final ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(final DomainEvent event) {
        publisher.publishEvent(event);
    }
}
