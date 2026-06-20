package dev.blumek.party.parties.web;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import tools.jackson.databind.ObjectMapper;
import dev.blumek.party.parties.application.PartyQueryService;
import dev.blumek.party.parties.application.PartyService;
import dev.blumek.party.parties.application.PartySummary;
import dev.blumek.party.parties.application.RegisterPerson;
import dev.blumek.party.parties.domain.IdentifierKind;
import dev.blumek.party.parties.domain.PartyError;
import dev.blumek.party.parties.domain.PartyId;
import dev.blumek.party.shared.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartyController.class)
class PartyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PartyService partyService;

    @MockitoBean
    private PartyQueryService queryService;

    private final PartyId partyId = PartyId.random();

    @Test
    void registeringAPersonReturnsCreatedWithItsSummary() throws Exception {
        givenRegistrationReturns(partyId);
        givenSummaryFor(partyId);

        mockMvc.perform(post("/parties/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(new RegisterPersonRequest("Ada", "Lovelace", LocalDate.of(1815, 12, 10)))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.kind").value("PERSON"))
                .andExpect(jsonPath("$.displayName").value("Ada Lovelace"));
    }

    private void givenRegistrationReturns(final PartyId id) {
        when(partyService.register(any(RegisterPerson.class))).thenReturn(Result.success(id));
    }

    private void givenSummaryFor(final PartyId id) {
        when(queryService.findById(id)).thenReturn(Optional.of(summary(id)));
    }

    private PartySummary summary(final PartyId id) {
        return new PartySummary(id.asString(), "PERSON", "Ada Lovelace", Set.of(), Set.of());
    }

    private String body(final Object request) throws Exception {
        return objectMapper.writeValueAsString(request);
    }

    @Test
    void fetchingAKnownPartyReturnsOk() throws Exception {
        givenSummaryFor(partyId);

        mockMvc.perform(get("/parties/{id}", partyId.asString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(partyId.asString()));
    }

    @Test
    void fetchingAnUnknownPartyReturnsNotFound() throws Exception {
        givenNoPartyFor(partyId);

        mockMvc.perform(get("/parties/{id}", partyId.asString()))
                .andExpect(status().isNotFound());
    }

    private void givenNoPartyFor(final PartyId id) {
        when(queryService.findById(id)).thenReturn(Optional.empty());
    }

    @Test
    void assigningARoleToAnUnknownPartyReturnsNotFound() throws Exception {
        givenAssignRoleReturns(Result.failure(new PartyError.PartyNotFound(partyId)));

        mockMvc.perform(post("/parties/{id}/roles", partyId.asString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(new AssignRoleRequest("Customer"))))
                .andExpect(status().isNotFound());
    }

    private void givenAssignRoleReturns(final Result<PartyError, PartyId> result) {
        when(partyService.assignRole(any())).thenReturn(result);
    }

    @Test
    void registeringAnIneligibleIdentifierReturnsUnprocessableEntity() throws Exception {
        givenRegisterIdentifierReturns(Result.failure(new PartyError.IdentifierNotEligible(IdentifierKind.TAX)));

        mockMvc.perform(post("/parties/{id}/identifiers", partyId.asString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(new RegisterIdentifierRequest("TAX", "1234567890"))))
                .andExpect(status().isUnprocessableContent());
    }

    private void givenRegisterIdentifierReturns(final Result<PartyError, PartyId> result) {
        when(partyService.registerIdentifier(any())).thenReturn(result);
    }
}
