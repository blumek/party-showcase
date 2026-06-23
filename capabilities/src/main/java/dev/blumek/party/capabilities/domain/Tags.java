package dev.blumek.party.capabilities.domain;

import java.util.Set;

import static dev.blumek.party.shared.Guards.require;
import static dev.blumek.party.shared.Guards.requireText;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toUnmodifiableSet;

final class Tags {

    private Tags() {
    }

    static Set<String> normalize(final Set<String> values, final String label) {
        require(values != null && !values.isEmpty(), label + " requires at least one value");
        return values.stream()
                .map(value -> requireText(value, label + " value cannot be blank").strip().toUpperCase(ROOT))
                .collect(toUnmodifiableSet());
    }

    static boolean canonical(final String value) {
        return value != null && !value.isBlank();
    }

    static String key(final String value) {
        return value.strip().toUpperCase(ROOT);
    }
}
