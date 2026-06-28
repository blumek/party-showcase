package dev.blumek.party.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void mapsADuplicateKeyConflictToHttp409() {
        var actualProblem = handler.onDuplicateKey();

        assertThat(actualProblem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }
}
