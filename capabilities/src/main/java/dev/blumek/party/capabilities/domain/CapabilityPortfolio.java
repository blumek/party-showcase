package dev.blumek.party.capabilities.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.blumek.party.shared.AggregateRoot;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;
import dev.blumek.party.shared.Version;

import static dev.blumek.party.shared.Guards.require;

public final class CapabilityPortfolio extends AggregateRoot<OwnerId> {

    private final OwnerId owner;
    private final Map<CapabilityId, Capability> capabilities = new LinkedHashMap<>();
    private Version version;

    private CapabilityPortfolio(final OwnerId owner) {
        require(owner != null, "Capability portfolio requires an owner");
        this.owner = owner;
        this.version = Version.initial();
    }

    public static CapabilityPortfolio openFor(final OwnerId owner) {
        return new CapabilityPortfolio(owner);
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

    public List<Capability> capabilities() {
        return List.copyOf(capabilities.values());
    }

    public Optional<Capability> find(final CapabilityId id) {
        return Optional.ofNullable(capabilities.get(id));
    }

    public Result<CapabilityError, CapabilityId> grant(final Capability capability) {
        require(capability != null, "Capability cannot be null");
        final var existing = capabilities.get(capability.id());
        return existing == null ? add(capability) : revise(existing, capability);
    }

    public Result<CapabilityError, CapabilityId> revoke(final CapabilityId id) {
        require(id != null, "Capability id cannot be null");
        final var removed = capabilities.remove(id);
        if (removed == null) {
            return Result.failure(new CapabilityError.CapabilityNotFound(id));
        }
        raise(new CapabilityRevoked(owner, id, removed.kind()));
        version = version.next();
        return Result.success(id);
    }

    private Result<CapabilityError, CapabilityId> add(final Capability capability) {
        store(capability);
        raise(new CapabilityGranted(owner, capability.id(), capability.kind()));
        return Result.success(capability.id());
    }

    private Result<CapabilityError, CapabilityId> revise(final Capability existing, final Capability revised) {
        if (!existing.differsFrom(revised)) {
            return Result.success(existing.id());
        }
        store(revised);
        raise(new CapabilityRevised(owner, revised.id(), revised.kind()));
        return Result.success(revised.id());
    }

    private void store(final Capability capability) {
        capabilities.put(capability.id(), capability);
        version = version.next();
    }
}
