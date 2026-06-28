package dev.blumek.party;

import dev.blumek.party.shared.DomainEvent;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SpringDomainEventPublisherTest {

    private final ApplicationEventPublisher springPublisher = mock(ApplicationEventPublisher.class);
    private final SpringDomainEventPublisher publisher = new SpringDomainEventPublisher(springPublisher);

    @Test
    void publishingForwardsTheEventToTheSpringApplicationEventPublisher() {
        final var event = givenDomainEvent();

        publisher.publish(event);

        thenSpringPublisherReceived(event);
    }

    private DomainEvent givenDomainEvent() {
        return new TestEvent();
    }

    private void thenSpringPublisherReceived(final DomainEvent event) {
        verify(springPublisher).publishEvent(event);
    }

    private record TestEvent() implements DomainEvent {
    }
}
