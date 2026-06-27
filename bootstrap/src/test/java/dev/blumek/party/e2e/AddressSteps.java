package dev.blumek.party.e2e;

import java.util.Set;

import dev.blumek.party.addresses.domain.AddressPurpose;
import dev.blumek.party.addresses.web.RecordEmailRequest;
import dev.blumek.party.addresses.web.RecordPostalRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class AddressSteps {

    private static final String ADDRESS = "address";

    private final ScenarioContext context;

    public AddressSteps(final ScenarioContext context) {
        this.context = context;
    }

    @Given("an email {string} is recorded for party {string} with purpose {string}")
    @When("I record an email {string} for party {string} with purpose {string}")
    public void recordEmail(final String email, final String alias, final String purpose) {
        final var request = new RecordEmailRequest(null, Set.of(AddressPurpose.valueOf(purpose)), null, null, email);
        final var response = given().contentType(ContentType.JSON)
                .body(request)
                .post("/parties/{partyId}/addresses/email", context.recall(alias));
        context.record(response);
        context.remember(ADDRESS, response.jsonPath().getString("id"));
    }

    @Given("a postal address is recorded for party {string} with purpose {string}")
    public void recordPostal(final String alias, final String purpose) {
        final var request = new RecordPostalRequest(null, Set.of(AddressPurpose.valueOf(purpose)), null, null,
                "221B Baker Street", null, "London", "NW1 6XE", "GB");
        context.record(given().contentType(ContentType.JSON)
                .body(request)
                .post("/parties/{partyId}/addresses/postal", context.recall(alias)));
    }

    @When("I fetch the recorded address for party {string}")
    public void fetchRecordedAddress(final String alias) {
        context.record(given().get("/parties/{partyId}/addresses/{addressId}",
                context.recall(alias), context.recall(ADDRESS)));
    }

    @When("I list the addresses for party {string}")
    public void listAddresses(final String alias) {
        context.record(given().get("/parties/{partyId}/addresses", context.recall(alias)));
    }

    @When("I remove the recorded address for party {string}")
    public void removeRecordedAddress(final String alias) {
        context.record(given().delete("/parties/{partyId}/addresses/{addressId}",
                context.recall(alias), context.recall(ADDRESS)));
    }

    @Then("the address has kind {string} and value {string}")
    public void theAddressHas(final String kind, final String value) {
        final var body = context.response().jsonPath();
        assertThat(body.getString("kind")).isEqualTo(kind);
        assertThat(body.getString("value")).isEqualTo(value);
    }

    @Then("the address list has size {int}")
    public void theAddressListHasSize(final int size) {
        assertThat(context.response().jsonPath().getList("id", String.class)).hasSize(size);
    }
}
