package dev.blumek.party.addresses.domain;

import java.util.regex.Pattern;

import static dev.blumek.party.shared.Guards.require;
import static dev.blumek.party.shared.Guards.requireText;
import static java.util.Locale.ROOT;

public record EmailAddress(String value) implements ContactPoint {

    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public EmailAddress {
        value = canonical(value);
    }

    private static String canonical(final String raw) {
        final var normalized = requireText(raw, "Email address cannot be blank")
                .strip()
                .toLowerCase(ROOT);
        require(FORMAT.matcher(normalized).matches(), "Email address is not well-formed: " + normalized);
        return normalized;
    }

    @Override
    public ContactKind kind() {
        return ContactKind.EMAIL;
    }
}
