package dev.blumek.party.addresses.web;

import java.util.Optional;
import java.util.Set;

import dev.blumek.party.addresses.application.AddressQueryService;
import dev.blumek.party.addresses.application.AddressService;
import dev.blumek.party.addresses.application.AddressSummary;
import dev.blumek.party.addresses.application.RecordEmailAddress;
import dev.blumek.party.addresses.domain.AddressError;
import dev.blumek.party.addresses.domain.AddressId;
import dev.blumek.party.addresses.domain.AddressPurpose;
import dev.blumek.party.addresses.domain.EmailAddress;
import dev.blumek.party.shared.OwnerId;
import dev.blumek.party.shared.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private AddressQueryService queryService;

    private final OwnerId owner = OwnerId.random();
    private final AddressId addressId = AddressId.random();

    @Test
    void recordingAnEmailReturnsCreatedWithItsSummary() throws Exception {
        givenRecordEmailReturns(Result.success(addressId));
        givenSummaryFor(addressId);

        mockMvc.perform(post("/parties/{partyId}/addresses/email", owner.asString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(new RecordEmailRequest(null, Set.of(AddressPurpose.NOTIFICATION), null, null,
                                "ada@example.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.kind").value("EMAIL"))
                .andExpect(jsonPath("$.value").value("ada@example.com"));
    }

    private void givenRecordEmailReturns(final Result<AddressError, AddressId> result) {
        when(addressService.recordEmail(any(RecordEmailAddress.class))).thenReturn(result);
    }

    private void givenSummaryFor(final AddressId id) {
        when(queryService.findById(any(OwnerId.class), eq(id))).thenReturn(Optional.of(summary(id)));
    }

    private AddressSummary summary(final AddressId id) {
        return new AddressSummary(id.asString(), "EMAIL", "ada@example.com", Set.of("NOTIFICATION"), null, null, null);
    }

    private String body(final Object request) throws Exception {
        return objectMapper.writeValueAsString(request);
    }

    @Test
    void recordingADuplicateContactReturnsConflict() throws Exception {
        givenRecordEmailReturns(Result.failure(new AddressError.DuplicateContact(new EmailAddress("ada@example.com"))));

        mockMvc.perform(post("/parties/{partyId}/addresses/email", owner.asString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(new RecordEmailRequest(null, Set.of(AddressPurpose.NOTIFICATION), null, null,
                                "ada@example.com"))))
                .andExpect(status().isConflict());
    }

    @Test
    void fetchingAKnownAddressReturnsOk() throws Exception {
        givenSummaryFor(addressId);

        mockMvc.perform(get("/parties/{partyId}/addresses/{addressId}", owner.asString(), addressId.asString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressId.asString()));
    }

    @Test
    void fetchingAnUnknownAddressReturnsNotFound() throws Exception {
        givenNoSummaryFor(addressId);

        mockMvc.perform(get("/parties/{partyId}/addresses/{addressId}", owner.asString(), addressId.asString()))
                .andExpect(status().isNotFound());
    }

    private void givenNoSummaryFor(final AddressId id) {
        when(queryService.findById(any(OwnerId.class), eq(id))).thenReturn(Optional.empty());
    }

    @Test
    void removingAKnownAddressReturnsNoContent() throws Exception {
        givenRemoveReturns(Result.success(addressId));

        mockMvc.perform(delete("/parties/{partyId}/addresses/{addressId}", owner.asString(), addressId.asString()))
                .andExpect(status().isNoContent());
    }

    private void givenRemoveReturns(final Result<AddressError, AddressId> result) {
        when(addressService.remove(any())).thenReturn(result);
    }

    @Test
    void removingAnUnknownAddressReturnsNotFound() throws Exception {
        givenRemoveReturns(Result.failure(new AddressError.AddressNotFound(addressId)));

        mockMvc.perform(delete("/parties/{partyId}/addresses/{addressId}", owner.asString(), addressId.asString()))
                .andExpect(status().isNotFound());
    }
}
