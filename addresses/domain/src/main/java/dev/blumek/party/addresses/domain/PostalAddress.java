package dev.blumek.party.addresses.domain;

import static dev.blumek.party.shared.Guards.require;
import static dev.blumek.party.shared.Guards.requireText;
import static java.util.Locale.ROOT;

public record PostalAddress(String line1, String line2, String city, PostalCode postalCode, String country)
        implements ContactPoint {

    public PostalAddress {
        line1 = requireText(line1, "Postal address requires a first line").strip();
        line2 = line2 == null ? null : line2.strip();
        city = requireText(city, "Postal address requires a city").strip();
        require(postalCode != null, "Postal address requires a postal code");
        country = countryCode(country);
    }

    private static String countryCode(final String raw) {
        final var code = requireText(raw, "Postal address requires a country").strip().toUpperCase(ROOT);
        require(code.matches("[A-Z]{2}"), "Country must be an ISO-3166 alpha-2 code: " + raw);
        return code;
    }

    @Override
    public ContactKind kind() {
        return ContactKind.POSTAL;
    }
}
