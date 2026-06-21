package dev.blumek.party.addresses.domain;

import java.util.Locale;

import dev.blumek.party.shared.Guards;

public record PostalAddress(String line1, String line2, String city, PostalCode postalCode, String country)
        implements ContactPoint {

    public PostalAddress {
        line1 = Guards.requireText(line1, "Postal address requires a first line").strip();
        line2 = line2 == null ? null : line2.strip();
        city = Guards.requireText(city, "Postal address requires a city").strip();
        Guards.require(postalCode != null, "Postal address requires a postal code");
        country = countryCode(country);
    }

    private static String countryCode(final String raw) {
        final var code = Guards.requireText(raw, "Postal address requires a country").strip().toUpperCase(Locale.ROOT);
        Guards.require(code.matches("[A-Z]{2}"), "Country must be an ISO-3166 alpha-2 code: " + raw);
        return code;
    }

    @Override
    public ContactKind kind() {
        return ContactKind.POSTAL;
    }
}
