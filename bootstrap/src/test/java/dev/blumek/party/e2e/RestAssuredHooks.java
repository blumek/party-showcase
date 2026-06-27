package dev.blumek.party.e2e;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Value;

public class RestAssuredHooks {

    private final int port;

    public RestAssuredHooks(@Value("${local.server.port}") final int port) {
        this.port = port;
    }

    @Before(order = 10)
    public void configureRestAssured() {
        RestAssured.port = port;
    }

    @After
    public void resetRestAssured() {
        RestAssured.reset();
    }
}
