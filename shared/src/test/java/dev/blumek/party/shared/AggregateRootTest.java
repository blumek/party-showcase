package dev.blumek.party.shared;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AggregateRootTest {

    @Test
    void exposesRaisedEventsInTheOrderTheyOccurred() {
        var box = givenBox();

        box.open();
        box.close();

        thenPendingEventsAre(box, new Opened(), new Closed());
    }

    private Box givenBox() {
        return new Box("box-1");
    }

    private void thenPendingEventsAre(final Box box, final DomainEvent... expected) {
        assertThat(box.domainEvents()).containsExactly(expected);
    }

    @Test
    void exposesPendingEventsAsAnImmutableSnapshot() {
        var box = givenBox();
        box.open();

        var actualSnapshot = box.domainEvents();
        box.close();

        thenSnapshotHasSize(actualSnapshot, 1);
    }

    private void thenSnapshotHasSize(final List<DomainEvent> snapshot, final int expected) {
        assertThat(snapshot).hasSize(expected);
    }

    @Test
    void dropsPendingEventsWhenCleared() {
        var box = givenBox();
        box.open();

        box.clearDomainEvents();

        thenNoPendingEvents(box);
    }

    private void thenNoPendingEvents(final Box box) {
        assertThat(box.domainEvents()).isEmpty();
    }

    private static final class Box extends AggregateRoot<String> {

        private final String id;

        private Box(final String id) {
            this.id = id;
        }

        @Override
        public String id() {
            return id;
        }

        void open() {
            raise(new Opened());
        }

        void close() {
            raise(new Closed());
        }
    }

    private record Opened() implements DomainEvent {
    }

    private record Closed() implements DomainEvent {
    }
}
