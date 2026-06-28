package dev.blumek.party;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dev.blumek.party.shared.DomainEvent;
import dev.blumek.party.shared.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("inmemory")
@Import(DomainEventDeliveryTest.RecordingListener.class)
class DomainEventDeliveryTest {

    @Autowired
    private DomainEventPublisher publisher;

    @Autowired
    private RecordingListener listener;

    @Test
    void aPublishedDomainEventIsDeliveredToSpringEventListeners() {
        final var event = givenDomainEvent();

        publisher.publish(event);

        thenListenerReceived(event);
    }

    private DomainEvent givenDomainEvent() {
        return new TestEvent();
    }

    private void thenListenerReceived(final DomainEvent event) {
        assertThat(listener.received).contains(event);
    }

    record TestEvent() implements DomainEvent {
    }

    static class RecordingListener {

        private final List<DomainEvent> received = new CopyOnWriteArrayList<>();

        @EventListener
        void on(final DomainEvent event) {
            received.add(event);
        }
    }
}
