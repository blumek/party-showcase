package dev.blumek.party.e2e;

import java.time.LocalDate;

import dev.blumek.party.parties.web.AssignRoleRequest;
import dev.blumek.party.parties.web.RegisterOrganizationRequest;
import dev.blumek.party.parties.web.RegisterPersonRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class PartySteps {

    private static final String CURRENT = "party";

    private final ScenarioContext context;

    public PartySteps(final ScenarioContext context) {
        this.context = context;
    }

    @Given("a registered person named {string} {string} born {string}")
    @When("I register a person named {string} {string} born {string}")
    public void registerPerson(final String given, final String family, final String dateOfBirth) {
        registerPersonAs(given, family, dateOfBirth, CURRENT);
    }

    @Given("a registered person named {string} {string} born {string} known as {string}")
    public void registerPersonAs(final String given, final String family, final String dateOfBirth,
            final String alias) {
        final var response = given().contentType(ContentType.JSON)
                .body(new RegisterPersonRequest(given, family, LocalDate.parse(dateOfBirth)))
                .post("/parties/people");
        rememberParty(response, alias);
    }

    @Given("a registered company named {string}")
    @When("I register a company named {string}")
    public void registerCompany(final String name) {
        registerCompanyAs(name, CURRENT);
    }

    @Given("a registered company named {string} known as {string}")
    public void registerCompanyAs(final String name, final String alias) {
        final var response = given().contentType(ContentType.JSON)
                .body(new RegisterOrganizationRequest(name))
                .post("/parties/companies");
        rememberParty(response, alias);
    }

    @Given("the role {string} is assigned to that party")
    @When("I assign the role {string} to that party")
    public void assignRole(final String role) {
        final var response = given().contentType(ContentType.JSON)
                .body(new AssignRoleRequest(role))
                .post("/parties/{id}/roles", context.recall(CURRENT));
        context.record(response);
    }

    @When("I fetch that party")
    public void fetchParty() {
        context.record(given().get("/parties/{id}", context.recall(CURRENT)));
    }

    @When("I search parties by role {string}")
    public void searchByRole(final String role) {
        context.record(given().queryParam("role", role).get("/parties"));
    }

    @When("I search parties by type {string} and name {string}")
    public void searchByTypeAndName(final String type, final String name) {
        context.record(given().queryParam("type", type).queryParam("name", name).get("/parties"));
    }

    @Then("the party has kind {string} and display name {string}")
    public void thePartyHas(final String kind, final String displayName) {
        final var body = context.response().jsonPath();
        assertThat(body.getString("kind")).isEqualTo(kind);
        assertThat(body.getString("displayName")).isEqualTo(displayName);
    }

    @Then("the party roles contain {string}")
    public void thePartyRolesContain(final String role) {
        assertThat(context.response().jsonPath().getList("roles", String.class)).contains(role);
    }

    @Then("the search returns {int} party with kind {string}")
    public void theSearchReturnsKind(final int count, final String kind) {
        assertThat(context.response().jsonPath().getList("kind", String.class)).hasSize(count).containsOnly(kind);
    }

    @Then("the search returns {int} party with display name {string}")
    public void theSearchReturnsDisplayName(final int count, final String displayName) {
        assertThat(context.response().jsonPath().getList("displayName", String.class))
                .hasSize(count).containsOnly(displayName);
    }

    private void rememberParty(final Response response, final String alias) {
        context.record(response);
        final var id = response.jsonPath().getString("id");
        context.remember(CURRENT, id);
        context.remember(alias, id);
    }
}
