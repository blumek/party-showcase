package dev.blumek.party.web;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("inmemory")
class OpenApiDocsTest {

    @Value("${local.server.port}")
    private int port;

    @Test
    void theOpenApiDocumentListsTheRegisteredApiPaths() {
        RestAssured.given().port(port)
                .get("/v3/api-docs")
                .then()
                .statusCode(200)
                .body("paths", hasKey("/parties/people"));
    }
}
