package dev.blumek.party.e2e;

import java.util.List;

import dev.blumek.party.capabilities.web.GrantCapabilityRequest;
import dev.blumek.party.capabilities.web.ScopeRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CapabilitySteps {

    private static final String CAPABILITY = "capability";

    private final ScenarioContext context;

    public CapabilitySteps(final ScenarioContext context) {
        this.context = context;
    }

    @When("I grant the {string} capability to party {string} with grade {string} rank {int}")
    public void grantCapability(final String kind, final String alias, final String grade, final int rank) {
        final var scope = new ScopeRequest("GRADE", null, grade, rank, null, null, null, null, null);
        final var request = new GrantCapabilityRequest(null, kind, List.of(scope), null, null);
        final var response = given().contentType(ContentType.JSON)
                .body(request)
                .post("/parties/{partyId}/capabilities", context.recall(alias));
        context.record(response);
        context.remember(CAPABILITY, response.jsonPath().getString("id"));
    }

    @When("I grant the {string} capability to party {string} with a grade scope missing its rank")
    public void grantGradeWithoutRank(final String kind, final String alias) {
        final var scope = new ScopeRequest("GRADE", null, "SENIOR", null, null, null, null, null, null);
        final var request = new GrantCapabilityRequest(null, kind, List.of(scope), null, null);
        context.record(given().contentType(ContentType.JSON)
                .body(request)
                .post("/parties/{partyId}/capabilities", context.recall(alias)));
    }

    @When("I fetch the granted capability for party {string}")
    public void fetchGrantedCapability(final String alias) {
        context.record(given().get("/parties/{partyId}/capabilities/{capabilityId}",
                context.recall(alias), context.recall(CAPABILITY)));
    }

    @When("I list the capabilities for party {string}")
    public void listCapabilities(final String alias) {
        context.record(given().get("/parties/{partyId}/capabilities", context.recall(alias)));
    }

    @When("I revoke the granted capability for party {string}")
    public void revokeCapability(final String alias) {
        context.record(given().delete("/parties/{partyId}/capabilities/{capabilityId}",
                context.recall(alias), context.recall(CAPABILITY)));
    }

    @Then("the capability has kind {string}")
    public void theCapabilityHasKind(final String kind) {
        assertThat(context.response().jsonPath().getString("kind")).isEqualTo(kind);
    }

    @Then("the first capability scope has dimension {string}")
    public void theFirstScopeHasDimension(final String dimension) {
        assertThat(context.response().jsonPath().getString("scopes[0].dimension")).isEqualTo(dimension);
    }

    @Then("the capability list has size {int}")
    public void theCapabilityListHasSize(final int size) {
        assertThat(context.response().jsonPath().getList("id", String.class)).hasSize(size);
    }
}
