package dev.blumek.party;

import java.time.LocalDate;

import dev.blumek.party.parties.application.PartySummary;
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
class PersistenceProfileIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registersAPersonAndReadsItBackFromTheDatabase() throws Exception {
        var id = givenRegisteredPerson();

        mockMvc.perform(get("/parties/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kind").value("PERSON"))
                .andExpect(jsonPath("$.displayName").value("Ada Lovelace"));
    }

    private String givenRegisteredPerson() throws Exception {
        var body = mockMvc.perform(post("/parties/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterPersonRequest("Ada", "Lovelace", LocalDate.of(1815, 12, 10)))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(body, PartySummary.class).id();
    }
}
