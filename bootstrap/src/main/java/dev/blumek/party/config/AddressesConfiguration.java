package dev.blumek.party.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.blumek.party.addresses.application.AddressQueryService;
import dev.blumek.party.addresses.application.AddressRepository;
import dev.blumek.party.addresses.application.AddressService;
import dev.blumek.party.shared.DomainEventPublisher;

@Configuration
class AddressesConfiguration {

    @Bean
    AddressService addressService(final AddressRepository repository, final DomainEventPublisher publisher) {
        return new AddressService(repository, publisher);
    }

    @Bean
    AddressQueryService addressQueryService(final AddressRepository repository) {
        return new AddressQueryService(repository);
    }
}
