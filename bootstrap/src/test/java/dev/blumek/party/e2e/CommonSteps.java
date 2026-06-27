package dev.blumek.party.e2e;

import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {

    private final ScenarioContext context;

    public CommonSteps(final ScenarioContext context) {
        this.context = context;
    }

    @Then("the response status is {int}")
    public void theResponseStatusIs(final int expected) {
        assertThat(context.response().statusCode()).isEqualTo(expected);
    }
}
