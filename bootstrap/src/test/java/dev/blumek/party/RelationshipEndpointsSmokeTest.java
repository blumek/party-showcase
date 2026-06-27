package dev.blumek.party;

import dev.blumek.party.relationships.application.RelationshipSummary;
import dev.blumek.party.relationships.web.EstablishRelationshipRequest;
import dev.blumek.party.shared.OwnerId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inmemory")
class RelationshipEndpointsSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void establishesARelationshipAndReadsItBack() throws Exception {
        var employer = OwnerId.random().asString();
        var employee = OwnerId.random().asString();
        var id = givenEstablishedRelationship(employer, employee);

        mockMvc.perform(get("/parties/{partyId}/relationships/{relationshipId}", employer, id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Employment"))
                .andExpect(jsonPath("$.toRole").value("Employee"));
    }

    private String givenEstablishedRelationship(final String employer, final String employee) throws Exception {
        var request = new EstablishRelationshipRequest(null, employee, "Employer", "Employee", "Employment", null, null);
        var body = mockMvc.perform(post("/parties/{partyId}/relationships", employer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(body, RelationshipSummary.class).id();
    }
}
