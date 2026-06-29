package dev.blumek.party.addresses.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PhoneNumberTest {

    @Test
    void reportsThePhoneKind() {
        var actualContact = new PhoneNumber("+44 20 7946 0958");

        thenKindIs(actualContact, ContactKind.PHONE);
    }

    private void thenKindIs(final ContactPoint contact, final ContactKind expected) {
        assertThat(contact.kind()).isEqualTo(expected);
    }

    @Test
    void stripsSeparatorsButKeepsTheInternationalPrefix() {
        var actualContact = new PhoneNumber("+44 (20) 7946-0958");

        thenValueIs(actualContact, "+442079460958");
    }

    private void thenValueIs(final PhoneNumber contact, final String expected) {
        assertThat(contact.value()).isEqualTo(expected);
    }

    @Test
    void stripsSeparatorsFromANationalNumber() {
        var actualContact = new PhoneNumber("020 7946 0958");

        thenValueIs(actualContact, "02079460958");
    }

    @Test
    void rejectsTooFewDigits() {
        var actualThrown = catchThrowable(() -> new PhoneNumber("12345"));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsTooManyDigits() {
        var actualThrown = catchThrowable(() -> new PhoneNumber("1234567890123456"));

        thenIllegalArgumentIsThrown(actualThrown);
    }
}
