package dev.blumek.party.addresses.domain;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.blumek.party.shared.AggregateRoot;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;
import dev.blumek.party.shared.Version;

import static dev.blumek.party.shared.Guards.require;

public final class AddressBook extends AggregateRoot<OwnerId> {

    private final OwnerId owner;
    private final AddressPolicy policy;
    private final Map<AddressId, Address> addresses = new LinkedHashMap<>();
    private Version version;

    private AddressBook(final OwnerId owner, final AddressPolicy policy) {
        require(owner != null, "Address book requires an owner");
        require(policy != null, "Address book requires a policy");
        this.owner = owner;
        this.policy = policy;
        this.version = Version.initial();
    }

    public static AddressBook openFor(final OwnerId owner) {
        return new AddressBook(owner, AddressPolicy.DEFAULT);
    }

    public static AddressBook openFor(final OwnerId owner, final AddressPolicy policy) {
        return new AddressBook(owner, policy);
    }

    public static AddressBook rehydrate(final OwnerId owner, final List<Address> addresses, final Version version) {
        final var book = new AddressBook(owner, AddressPolicy.DEFAULT);
        addresses.forEach(address -> book.addresses.put(address.id(), address));
        book.version = version;
        return book;
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

    public List<Address> addresses() {
        return List.copyOf(addresses.values());
    }

    public Optional<Address> find(final AddressId id) {
        return Optional.ofNullable(addresses.get(id));
    }

    public Result<AddressError, AddressId> record(final Address address) {
        require(address != null, "Address cannot be null");
        final var existing = addresses.get(address.id());
        return existing == null ? add(address) : revise(existing, address);
    }

    public Result<AddressError, AddressId> remove(final AddressId id) {
        require(id != null, "Address id cannot be null");
        final var removed = addresses.remove(id);
        if (removed == null) {
            return Result.failure(new AddressError.AddressNotFound(id));
        }
        raise(new AddressWithdrawn(owner, id, removed.kind()));
        version = version.next();
        return Result.success(id);
    }

    private Result<AddressError, AddressId> add(final Address address) {
        return policy.check(address, addresses.values())
                .<Result<AddressError, AddressId>>map(Result::failure)
                .orElseGet(() -> {
                    store(address);
                    raise(new AddressRecorded(owner, address.id(), address.kind()));
                    return Result.success(address.id());
                });
    }

    private Result<AddressError, AddressId> revise(final Address existing, final Address revised) {
        if (!existing.sameKindAs(revised)) {
            return Result.failure(new AddressError.KindMismatch(existing.id(), existing.kind(), revised.kind()));
        }
        if (!existing.differsFrom(revised)) {
            return Result.success(existing.id());
        }
        return policy.check(revised, others(revised.id()))
                .<Result<AddressError, AddressId>>map(Result::failure)
                .orElseGet(() -> {
                    store(revised);
                    raise(new AddressRevised(owner, revised.id(), revised.kind()));
                    return Result.success(revised.id());
                });
    }

    private Collection<Address> others(final AddressId excluded) {
        return addresses.values().stream()
                .filter(address -> !address.id().equals(excluded))
                .toList();
    }

    private void store(final Address address) {
        addresses.put(address.id(), address);
        version = version.next();
    }
}
