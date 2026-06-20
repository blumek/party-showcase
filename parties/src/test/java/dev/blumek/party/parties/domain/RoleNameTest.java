package dev.blumek.party.parties.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class RoleNameTest {

    @Test
    void trimsSurroundingWhitespace() {
        var actualRoleName = new RoleName("  Customer  ");

        thenValueIs(actualRoleName, "Customer");
    }

    private void thenValueIs(final RoleName roleName, final String expected) {
        assertThat(roleName.value()).isEqualTo(expected);
    }

    @Test
    void rejectsBlankText() {
        var actualThrown = catchThrowable(() -> new RoleName("   "));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
