package dev.blumek.party.addresses.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PostalAddressTest {

    @Test
    void reportsThePostalKind() {
        var actualContact = givenAddressIn("GB");

        thenKindIs(actualContact, ContactKind.POSTAL);
    }

    private PostalAddress givenAddressIn(final String country) {
        return new PostalAddress("221B Baker Street", null, "London", new PostalCode("NW1 6XE"), country);
    }

    private void thenKindIs(final ContactPoint contact, final ContactKind expected) {
        assertThat(contact.kind()).isEqualTo(expected);
    }

    @Test
    void upperCasesTheCountryCode() {
        var actualContact = givenAddressIn("gb");

        thenCountryIs(actualContact, "GB");
    }

    private void thenCountryIs(final PostalAddress contact, final String expected) {
        assertThat(contact.country()).isEqualTo(expected);
    }

    @Test
    void rejectsACountryThatIsNotTwoLetters() {
        var actualThrown = catchThrowable(() -> givenAddressIn("GBR"));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsABlankFirstLine() {
        var actualThrown = catchThrowable(
                () -> new PostalAddress("  ", null, "London", new PostalCode("NW1 6XE"), "GB"));

        thenIllegalArgumentIsThrown(actualThrown);
    }
}
