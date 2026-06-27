package dev.blumek.party.e2e;

import java.util.HashMap;
import java.util.Map;

import io.restassured.response.Response;
import org.springframework.stereotype.Component;

@Component
class ScenarioContext {

    private final Map<String, String> values = new HashMap<>();
    private Response response;

    void reset() {
        values.clear();
        response = null;
    }

    void record(final Response response) {
        this.response = response;
    }

    Response response() {
        return response;
    }

    void remember(final String key, final String value) {
        values.put(key, value);
    }

    String recall(final String key) {
        return values.get(key);
    }
}
