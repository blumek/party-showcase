package dev.blumek.party.shared;

import java.util.Collection;

public interface DomainEventPublisher {

    void publish(DomainEvent event);

    default void publishAll(final Collection<? extends DomainEvent> events) {
        events.forEach(this::publish);
    }
}
