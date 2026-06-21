package dev.blumek.party.addresses.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class EmailAddressTest {

    @Test
    void reportsTheEmailKind() {
        var actualContact = new EmailAddress("ada@example.com");

        thenKindIs(actualContact, ContactKind.EMAIL);
    }

    private void thenKindIs(final ContactPoint contact, final ContactKind expected) {
        assertThat(contact.kind()).isEqualTo(expected);
    }

    @Test
    void lowerCasesAndTrimsToACanonicalValue() {
        var actualContact = new EmailAddress("  Ada@Example.COM ");

        thenValueIs(actualContact, "ada@example.com");
    }

    private void thenValueIs(final EmailAddress contact, final String expected) {
        assertThat(contact.value()).isEqualTo(expected);
    }

    @Test
    void rejectsAnAddressWithoutADomainDot() {
        var actualThrown = catchThrowable(() -> new EmailAddress("ada@example"));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAnAddressWithoutAnAtSign() {
        var actualThrown = catchThrowable(() -> new EmailAddress("ada.example.com"));

        thenIllegalArgumentIsThrown(actualThrown);
    }
}
