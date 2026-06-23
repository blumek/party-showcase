package dev.blumek.party;

import java.util.List;

import dev.blumek.party.capabilities.application.CapabilitySummary;
import dev.blumek.party.capabilities.web.GrantCapabilityRequest;
import dev.blumek.party.capabilities.web.ScopeRequest;
import dev.blumek.party.shared.OwnerId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CapabilityEndpointsSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void grantsACapabilityAndReadsItBack() throws Exception {
        var owner = OwnerId.random().asString();
        var id = givenGrantedCapability(owner);

        mockMvc.perform(get("/parties/{partyId}/capabilities/{capabilityId}", owner, id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kind").value("MedicalImaging"))
                .andExpect(jsonPath("$.scopes[0].dimension").value("GRADE"));
    }

    private String givenGrantedCapability(final String owner) throws Exception {
        var grade = new ScopeRequest("GRADE", null, "SENIOR", 3, null, null, null, null, null);
        var request = new GrantCapabilityRequest(null, "MedicalImaging", List.of(grade), null, null);
        var body = mockMvc.perform(post("/parties/{partyId}/capabilities", owner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(body, CapabilitySummary.class).id();
    }
}
