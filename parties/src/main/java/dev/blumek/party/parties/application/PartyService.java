package dev.blumek.party.parties.application;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import dev.blumek.party.parties.domain.Company;
import dev.blumek.party.parties.domain.Organization;
import dev.blumek.party.parties.domain.OrganizationUnit;
import dev.blumek.party.parties.domain.Party;
import dev.blumek.party.parties.domain.PartyError;
import dev.blumek.party.parties.domain.PartyId;
import dev.blumek.party.parties.domain.Person;
import dev.blumek.party.shared.DomainEventPublisher;
import dev.blumek.party.shared.Result;

@Service
public class PartyService {

    private final PartyStore store;
    private final DomainEventPublisher publisher;

    public PartyService(final PartyStore store, final DomainEventPublisher publisher) {
        this.store = store;
        this.publisher = publisher;
    }

    public Result<PartyError, PartyId> register(final RegisterPerson command) {
        return persisted(Person.register(command.profile()));
    }

    public Result<PartyError, PartyId> register(final RegisterCompany command) {
        return persisted(Company.register(command.name()));
    }

    public Result<PartyError, PartyId> register(final RegisterOrganizationUnit command) {
        return persisted(OrganizationUnit.register(command.name()));
    }

    public Result<PartyError, PartyId> assignRole(final AssignRole command) {
        return apply(command.partyId(), party -> party.assignRole(command.role()));
    }

    public Result<PartyError, PartyId> relinquishRole(final RelinquishRole command) {
        return apply(command.partyId(), party -> party.relinquishRole(command.role()));
    }

    public Result<PartyError, PartyId> registerIdentifier(final RegisterIdentifier command) {
        return apply(command.partyId(), party -> party.registerIdentifier(command.identifier()));
    }

    public Result<PartyError, PartyId> withdrawIdentifier(final WithdrawIdentifier command) {
        return apply(command.partyId(), party -> party.withdrawIdentifier(command.identifier()));
    }

    public Result<PartyError, PartyId> updateProfile(final UpdatePersonProfile command) {
        return apply(command.partyId(), party -> {
            if (party instanceof Person person) {
                person.updateProfile(command.profile());
                return Result.success(party);
            }
            return Result.failure(new PartyError.PartyNotFound(command.partyId()));
        });
    }

    public Result<PartyError, PartyId> rename(final RenameOrganization command) {
        return apply(command.partyId(), party -> {
            if (party instanceof Organization organization) {
                organization.rename(command.name());
                return Result.success(party);
            }
            return Result.failure(new PartyError.PartyNotFound(command.partyId()));
        });
    }

    private Result<PartyError, PartyId> apply(final PartyId id,
            final Function<Party, Result<PartyError, Party>> action) {
        return store.findById(id)
                .map(party -> action.apply(party).onSuccess(this::persist).map(Party::id))
                .orElseGet(() -> Result.failure(new PartyError.PartyNotFound(id)));
    }

    private Result<PartyError, PartyId> persisted(final Party party) {
        persist(party);
        return Result.success(party.id());
    }

    private void persist(final Party party) {
        store.save(party);
        publisher.publishAll(party.domainEvents());
        party.clearDomainEvents();
    }
}
