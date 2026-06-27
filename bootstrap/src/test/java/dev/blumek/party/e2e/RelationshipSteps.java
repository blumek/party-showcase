package dev.blumek.party.e2e;

import dev.blumek.party.relationships.web.EstablishRelationshipRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class RelationshipSteps {

    private static final String RELATIONSHIP = "relationship";

    private final ScenarioContext context;

    public RelationshipSteps(final ScenarioContext context) {
        this.context = context;
    }

    @When("I establish an {string} relationship from {string} as {string} to {string} as {string}")
    public void establish(final String type, final String fromAlias, final String fromRole,
            final String toAlias, final String toRole) {
        final var request = new EstablishRelationshipRequest(null, context.recall(toAlias), fromRole, toRole, type,
                null, null);
        final var response = given().contentType(ContentType.JSON)
                .body(request)
                .post("/parties/{partyId}/relationships", context.recall(fromAlias));
        context.record(response);
        context.remember(RELATIONSHIP, response.jsonPath().getString("id"));
    }

    @When("I fetch the established relationship from {string}")
    public void fetchEstablished(final String fromAlias) {
        context.record(given().get("/parties/{partyId}/relationships/{relationshipId}",
                context.recall(fromAlias), context.recall(RELATIONSHIP)));
    }

    @Given("an {string} relationship from {string} as {string} to {string} as {string}")
    public void anEstablishedRelationship(final String type, final String fromAlias, final String fromRole,
            final String toAlias, final String toRole) {
        establish(type, fromAlias, fromRole, toAlias, toRole);
    }

    @When("I list the relationships from {string}")
    public void listRelationships(final String fromAlias) {
        context.record(given().get("/parties/{partyId}/relationships", context.recall(fromAlias)));
    }

    @When("I list {string} {string} relationships from {string}")
    public void listFiltered(final String direction, final String type, final String fromAlias) {
        context.record(given().queryParam("direction", direction).queryParam("type", type)
                .get("/parties/{partyId}/relationships", context.recall(fromAlias)));
    }

    @When("I end the established relationship from {string}")
    public void endRelationship(final String fromAlias) {
        context.record(given().delete("/parties/{partyId}/relationships/{relationshipId}",
                context.recall(fromAlias), context.recall(RELATIONSHIP)));
    }

    @Then("the relationship has type {string} and to-role {string}")
    public void theRelationshipHas(final String type, final String toRole) {
        final var body = context.response().jsonPath();
        assertThat(body.getString("type")).isEqualTo(type);
        assertThat(body.getString("toRole")).isEqualTo(toRole);
    }

    @Then("the relationship list has size {int}")
    public void theRelationshipListHasSize(final int size) {
        assertThat(context.response().jsonPath().getList("id", String.class)).hasSize(size);
    }
}
