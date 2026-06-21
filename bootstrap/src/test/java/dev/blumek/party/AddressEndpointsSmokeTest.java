package dev.blumek.party;

import java.util.Set;

import dev.blumek.party.addresses.application.AddressSummary;
import dev.blumek.party.addresses.domain.AddressPurpose;
import dev.blumek.party.addresses.web.RecordEmailRequest;
import dev.blumek.party.addresses.web.RecordPostalRequest;
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
class AddressEndpointsSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void recordsAnEmailAndReadsItBack() throws Exception {
        var owner = OwnerId.random().asString();
        var id = givenRecordedEmail(owner, "ada@example.com");

        mockMvc.perform(get("/parties/{partyId}/addresses/{addressId}", owner, id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kind").value("EMAIL"))
                .andExpect(jsonPath("$.value").value("ada@example.com"));
    }

    private String givenRecordedEmail(final String owner, final String email) throws Exception {
        var body = mockMvc.perform(post("/parties/{partyId}/addresses/email", owner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RecordEmailRequest(null, Set.of(AddressPurpose.NOTIFICATION), null, null, email))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(body, AddressSummary.class).id();
    }

    @Test
    void recordsAnEmailAndAPostalAddressAndListsThem() throws Exception {
        var owner = OwnerId.random().asString();
        givenRecordedEmail(owner, "ada@example.com");
        givenRecordedPostalAddress(owner);

        mockMvc.perform(get("/parties/{partyId}/addresses", owner))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private void givenRecordedPostalAddress(final String owner) throws Exception {
        mockMvc.perform(post("/parties/{partyId}/addresses/postal", owner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RecordPostalRequest(null,
                                Set.of(AddressPurpose.RESIDENCE), null, null, "221B Baker Street", null, "London",
                                "NW1 6XE", "GB"))))
                .andExpect(status().isCreated());
    }
}
