package dev.blumek.party.shared;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventPublisherTest {

    private final List<DomainEvent> publishedEvents = new ArrayList<>();

    @Test
    void publishAllForwardsEveryEventToPublishInOrder() {
        var publisher = givenRecordingPublisher();

        publisher.publishAll(List.of(new Raised(), new Lowered()));

        thenPublishedEventsAre(new Raised(), new Lowered());
    }

    private DomainEventPublisher givenRecordingPublisher() {
        return publishedEvents::add;
    }

    private void thenPublishedEventsAre(final DomainEvent... expected) {
        assertThat(publishedEvents).containsExactly(expected);
    }

    private record Raised() implements DomainEvent {
    }

    private record Lowered() implements DomainEvent {
    }
}
