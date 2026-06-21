package dev.blumek.party.addresses.domain;

import java.util.Locale;
import java.util.regex.Pattern;

import dev.blumek.party.shared.Guards;

public record EmailAddress(String value) implements ContactPoint {

    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public EmailAddress {
        value = canonical(value);
    }

    private static String canonical(final String raw) {
        final var normalized = Guards.requireText(raw, "Email address cannot be blank")
                .strip()
                .toLowerCase(Locale.ROOT);
        Guards.require(FORMAT.matcher(normalized).matches(), "Email address is not well-formed: " + normalized);
        return normalized;
    }

    @Override
    public ContactKind kind() {
        return ContactKind.EMAIL;
    }
}
