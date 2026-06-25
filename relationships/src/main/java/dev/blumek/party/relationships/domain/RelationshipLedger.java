package dev.blumek.party.relationships.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.blumek.party.shared.AggregateRoot;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;
import dev.blumek.party.shared.Version;

import static dev.blumek.party.shared.Guards.require;

public final class RelationshipLedger extends AggregateRoot<OwnerId> {

    private final OwnerId owner;
    private final RelationshipPolicy policy;
    private final Map<RelationshipId, Relationship> relationships = new LinkedHashMap<>();
    private Version version;

    private RelationshipLedger(final OwnerId owner, final RelationshipPolicy policy) {
        require(owner != null, "Relationship ledger requires an owner");
        require(policy != null, "Relationship ledger requires a policy");
        this.owner = owner;
        this.policy = policy;
        this.version = Version.initial();
    }

    public static RelationshipLedger openFor(final OwnerId owner) {
        return new RelationshipLedger(owner, RelationshipPolicy.DEFAULT);
    }

    public static RelationshipLedger openFor(final OwnerId owner, final RelationshipPolicy policy) {
        return new RelationshipLedger(owner, policy);
    }

    @Override
    public OwnerId id() {
        return owner;
    }

    public OwnerId owner() {
        return owner;
    }

    public Version version() {
        return version;
    }

    public List<Relationship> relationships() {
        return List.copyOf(relationships.values());
    }

    public Optional<Relationship> find(final RelationshipId id) {
        return Optional.ofNullable(relationships.get(id));
    }

    public Result<RelationshipError, RelationshipId> establish(final Relationship relationship) {
        require(relationship != null, "Relationship cannot be null");
        final var existing = relationships.get(relationship.id());
        return existing == null ? add(relationship) : revise(existing, relationship);
    }

    public Result<RelationshipError, RelationshipId> terminate(final RelationshipId id) {
        require(id != null, "Relationship id cannot be null");
        final var removed = relationships.remove(id);
        if (removed == null) {
            return Result.failure(new RelationshipError.RelationshipNotFound(id));
        }
        raise(new RelationshipTerminated(owner, id, removed.type()));
        version = version.next();
        return Result.success(id);
    }

    private Result<RelationshipError, RelationshipId> add(final Relationship relationship) {
        return policy.check(relationship.from(), relationship.to(), relationship.type())
                .<Result<RelationshipError, RelationshipId>>map(Result::failure)
                .orElseGet(() -> {
                    store(relationship);
                    raise(new RelationshipEstablished(owner, relationship.id(), relationship.type()));
                    return Result.success(relationship.id());
                });
    }

    private Result<RelationshipError, RelationshipId> revise(final Relationship existing, final Relationship revised) {
        if (!existing.differsFrom(revised)) {
            return Result.success(existing.id());
        }
        return policy.check(revised.from(), revised.to(), revised.type())
                .<Result<RelationshipError, RelationshipId>>map(Result::failure)
                .orElseGet(() -> {
                    store(revised);
                    raise(new RelationshipRevised(owner, revised.id(), revised.type()));
                    return Result.success(revised.id());
                });
    }

    private void store(final Relationship relationship) {
        relationships.put(relationship.id(), relationship);
        version = version.next();
    }
}
