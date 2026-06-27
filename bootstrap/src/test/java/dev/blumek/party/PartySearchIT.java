package dev.blumek.party;

import java.time.LocalDate;
import java.util.UUID;

import dev.blumek.party.parties.application.PartySummary;
import dev.blumek.party.parties.web.AssignRoleRequest;
import dev.blumek.party.parties.web.RegisterOrganizationRequest;
import dev.blumek.party.parties.web.RegisterPersonRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
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
@ActiveProfiles("jdbc")
@Import(PostgresContainerSupport.class)
class PartySearchIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findsAPersonByRoleOverHttp() throws Exception {
        var role = "Role-" + UUID.randomUUID();
        var id = givenRegisteredPerson("Ada", "Lovelace");
        givenRoleAssigned(id, role);

        mockMvc.perform(get("/parties").param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].kind").value("PERSON"));
    }

    @Test
    void findsACompanyByTypeAndNameOverHttp() throws Exception {
        var name = "Acme-" + UUID.randomUUID();
        givenRegisteredCompany(name);

        mockMvc.perform(get("/parties").param("type", "COMPANY").param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].displayName").value(name));
    }

    private String givenRegisteredPerson(final String given, final String family) throws Exception {
        var body = mockMvc.perform(post("/parties/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterPersonRequest(given, family, LocalDate.of(1815, 12, 10)))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(body, PartySummary.class).id();
    }

    private void givenRoleAssigned(final String id, final String role) throws Exception {
        mockMvc.perform(post("/parties/{id}/roles", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssignRoleRequest(role))))
                .andExpect(status().isOk());
    }

    private void givenRegisteredCompany(final String name) throws Exception {
        mockMvc.perform(post("/parties/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterOrganizationRequest(name))))
                .andExpect(status().isCreated());
    }
}
