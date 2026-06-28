package dev.blumek.party.relationships.web;

import java.util.List;
import java.util.Optional;

import dev.blumek.party.relationships.application.EstablishRelationship;
import dev.blumek.party.relationships.application.RelationshipFinder;
import dev.blumek.party.relationships.application.RelationshipQuery;
import dev.blumek.party.relationships.application.RelationshipQueryService;
import dev.blumek.party.relationships.application.RelationshipService;
import dev.blumek.party.relationships.application.RelationshipSummary;
import dev.blumek.party.relationships.domain.RelationshipError;
import dev.blumek.party.relationships.domain.RelationshipId;
import dev.blumek.party.relationships.domain.RelationshipType;
import dev.blumek.party.relationships.domain.Role;
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

@WebMvcTest(RelationshipController.class)
class RelationshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RelationshipService relationshipService;

    @MockitoBean
    private RelationshipQueryService queryService;

    @MockitoBean
    private RelationshipFinder finder;

    private final OwnerId owner = OwnerId.random();
    private final OwnerId other = OwnerId.random();
    private final RelationshipId relationshipId = RelationshipId.random();

    @Test
    void establishingARelationshipReturnsCreatedWithItsSummary() throws Exception {
        givenEstablishReturns(Result.success(relationshipId));
        givenSummaryFor(relationshipId);

        mockMvc.perform(post("/parties/{partyId}/relationships", owner.asString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(establishRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("Employment"))
                .andExpect(jsonPath("$.toRole").value("Employee"));
    }

    private void givenEstablishReturns(final Result<RelationshipError, RelationshipId> result) {
        when(relationshipService.establish(any(EstablishRelationship.class))).thenReturn(result);
    }

    private void givenSummaryFor(final RelationshipId id) {
        when(queryService.findById(any(OwnerId.class), eq(id))).thenReturn(Optional.of(summary(id)));
    }

    private RelationshipSummary summary(final RelationshipId id) {
        return new RelationshipSummary(id.asString(), owner.asString(), "Employer", other.asString(), "Employee",
                "Employment", null, null);
    }

    private EstablishRelationshipRequest establishRequest() {
        return new EstablishRelationshipRequest(other.asString(), "Employer", "Employee", "Employment",
                null, null);
    }

    private String body(final Object request) throws Exception {
        return objectMapper.writeValueAsString(request);
    }

    @Test
    void establishingWithDisallowedRolesReturnsConflict() throws Exception {
        givenEstablishReturns(Result.failure(
                new RelationshipError.RolesNotAllowed(RelationshipType.of("Employment"),
                        Role.of("Employee"), Role.of("Employer"))));

        mockMvc.perform(post("/parties/{partyId}/relationships", owner.asString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(establishRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    void listingEmployeesAppliesDirectionTypeAndRoleFilters() throws Exception {
        var query = new RelationshipQuery(owner, RelationshipQuery.Direction.OUTGOING, "Employment", "Employee");
        when(finder.find(eq(query))).thenReturn(List.of(summary(relationshipId)));

        mockMvc.perform(get("/parties/{partyId}/relationships", owner.asString())
                        .param("direction", "OUTGOING")
                        .param("type", "Employment")
                        .param("role", "Employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].toRole").value("Employee"));
    }

    @Test
    void listingDefaultsToOutgoingDirection() throws Exception {
        var query = new RelationshipQuery(owner, RelationshipQuery.Direction.OUTGOING, null, null);
        when(finder.find(eq(query))).thenReturn(List.of(summary(relationshipId)));

        mockMvc.perform(get("/parties/{partyId}/relationships", owner.asString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(relationshipId.asString()));
    }

    @Test
    void fetchingAKnownRelationshipReturnsOk() throws Exception {
        givenSummaryFor(relationshipId);

        mockMvc.perform(get("/parties/{partyId}/relationships/{relationshipId}", owner.asString(), relationshipId.asString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(relationshipId.asString()));
    }

    @Test
    void fetchingAnUnknownRelationshipReturnsNotFound() throws Exception {
        givenNoSummaryFor(relationshipId);

        mockMvc.perform(get("/parties/{partyId}/relationships/{relationshipId}", owner.asString(), relationshipId.asString()))
                .andExpect(status().isNotFound());
    }

    private void givenNoSummaryFor(final RelationshipId id) {
        when(queryService.findById(any(OwnerId.class), eq(id))).thenReturn(Optional.empty());
    }

    @Test
    void terminatingAKnownRelationshipReturnsNoContent() throws Exception {
        givenTerminateReturns(Result.success(relationshipId));

        mockMvc.perform(delete("/parties/{partyId}/relationships/{relationshipId}", owner.asString(), relationshipId.asString()))
                .andExpect(status().isNoContent());
    }

    private void givenTerminateReturns(final Result<RelationshipError, RelationshipId> result) {
        when(relationshipService.terminate(any())).thenReturn(result);
    }

    @Test
    void terminatingAnUnknownRelationshipReturnsNotFound() throws Exception {
        givenTerminateReturns(Result.failure(new RelationshipError.RelationshipNotFound(relationshipId)));

        mockMvc.perform(delete("/parties/{partyId}/relationships/{relationshipId}", owner.asString(), relationshipId.asString()))
                .andExpect(status().isNotFound());
    }
}
