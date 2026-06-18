package dev.blumek.party.shared;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot<ID> {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public abstract ID id();

    protected void raise(final DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> domainEvents() {
        return List.copyOf(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }
}
