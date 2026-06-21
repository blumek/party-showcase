package dev.blumek.party.addresses.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class WebsiteUrlTest {

    @Test
    void reportsTheWebsiteKind() {
        var actualContact = new WebsiteUrl("https://example.com");

        thenKindIs(actualContact, ContactKind.WEBSITE);
    }

    private void thenKindIs(final ContactPoint contact, final ContactKind expected) {
        assertThat(contact.kind()).isEqualTo(expected);
    }

    @Test
    void acceptsAnHttpsUrl() {
        var actualContact = new WebsiteUrl("https://example.com/contact");

        thenValueIs(actualContact, "https://example.com/contact");
    }

    private void thenValueIs(final WebsiteUrl contact, final String expected) {
        assertThat(contact.value()).isEqualTo(expected);
    }

    @Test
    void rejectsANonHttpScheme() {
        var actualThrown = catchThrowable(() -> new WebsiteUrl("ftp://example.com"));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAUrlWithoutAHost() {
        var actualThrown = catchThrowable(() -> new WebsiteUrl("https:///path"));

        thenIllegalArgumentIsThrown(actualThrown);
    }
}
