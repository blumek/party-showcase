package dev.blumek.party.capabilities.web;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import dev.blumek.party.capabilities.application.CapabilityQueryService;
import dev.blumek.party.capabilities.application.CapabilityService;
import dev.blumek.party.capabilities.application.CapabilitySummary;
import dev.blumek.party.capabilities.application.GrantCapability;
import dev.blumek.party.capabilities.domain.CapabilityError;
import dev.blumek.party.capabilities.domain.CapabilityId;
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

@WebMvcTest(CapabilityController.class)
class CapabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CapabilityService capabilityService;

    @MockitoBean
    private CapabilityQueryService queryService;

    private final OwnerId owner = OwnerId.random();
    private final CapabilityId capabilityId = CapabilityId.random();

    @Test
    void grantingACapabilityReturnsCreatedWithItsSummary() throws Exception {
        givenGrantReturns(Result.success(capabilityId));
        givenSummaryFor(capabilityId);

        mockMvc.perform(post("/parties/{partyId}/capabilities", owner.asString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(grantRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.kind").value("MedicalImaging"))
                .andExpect(jsonPath("$.scopes[0].dimension").value("GRADE"));
    }

    private void givenGrantReturns(final Result<CapabilityError, CapabilityId> result) {
        when(capabilityService.grant(any(GrantCapability.class))).thenReturn(result);
    }

    private void givenSummaryFor(final CapabilityId id) {
        when(queryService.findById(any(OwnerId.class), eq(id))).thenReturn(Optional.of(summary(id)));
    }

    private CapabilitySummary summary(final CapabilityId id) {
        return new CapabilitySummary(id.asString(), owner.asString(), "MedicalImaging",
                Set.of(new CapabilitySummary.ScopeSummary("GRADE", "SENIOR")), null, null);
    }

    private GrantCapabilityRequest grantRequest() {
        var grade = new ScopeRequest("GRADE", null, "SENIOR", 3, null, null, null, null, null);
        return new GrantCapabilityRequest(null, "MedicalImaging", List.of(grade), null, null);
    }

    private String body(final Object request) throws Exception {
        return objectMapper.writeValueAsString(request);
    }

    @Test
    void fetchingAKnownCapabilityReturnsOk() throws Exception {
        givenSummaryFor(capabilityId);

        mockMvc.perform(get("/parties/{partyId}/capabilities/{capabilityId}", owner.asString(), capabilityId.asString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(capabilityId.asString()));
    }

    @Test
    void fetchingAnUnknownCapabilityReturnsNotFound() throws Exception {
        givenNoSummaryFor(capabilityId);

        mockMvc.perform(get("/parties/{partyId}/capabilities/{capabilityId}", owner.asString(), capabilityId.asString()))
                .andExpect(status().isNotFound());
    }

    private void givenNoSummaryFor(final CapabilityId id) {
        when(queryService.findById(any(OwnerId.class), eq(id))).thenReturn(Optional.empty());
    }

    @Test
    void revokingAKnownCapabilityReturnsNoContent() throws Exception {
        givenRevokeReturns(Result.success(capabilityId));

        mockMvc.perform(delete("/parties/{partyId}/capabilities/{capabilityId}", owner.asString(), capabilityId.asString()))
                .andExpect(status().isNoContent());
    }

    private void givenRevokeReturns(final Result<CapabilityError, CapabilityId> result) {
        when(capabilityService.revoke(any())).thenReturn(result);
    }

    @Test
    void revokingAnUnknownCapabilityReturnsNotFound() throws Exception {
        givenRevokeReturns(Result.failure(new CapabilityError.CapabilityNotFound(capabilityId)));

        mockMvc.perform(delete("/parties/{partyId}/capabilities/{capabilityId}", owner.asString(), capabilityId.asString()))
                .andExpect(status().isNotFound());
    }
}
