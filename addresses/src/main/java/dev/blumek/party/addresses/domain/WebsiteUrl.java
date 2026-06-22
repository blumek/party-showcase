package dev.blumek.party.addresses.domain;

import java.net.URI;

import static dev.blumek.party.shared.Guards.require;
import static dev.blumek.party.shared.Guards.requireText;
import static java.util.Locale.ROOT;

public record WebsiteUrl(String value) implements ContactPoint {

    public WebsiteUrl {
        value = canonical(value);
    }

    private static String canonical(final String raw) {
        final var text = requireText(raw, "Website URL cannot be blank").strip();
        final var uri = parse(text);
        final var scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(ROOT);
        require(scheme.equals("http") || scheme.equals("https"), "Website URL must use http or https: " + text);
        require(uri.getHost() != null, "Website URL must have a host: " + text);
        return uri.toString();
    }

    private static URI parse(final String text) {
        try {
            return URI.create(text);
        } catch (final IllegalArgumentException cause) {
            throw new IllegalArgumentException("Website URL is not a valid URI: " + text, cause);
        }
    }

    @Override
    public ContactKind kind() {
        return ContactKind.WEBSITE;
    }
}
