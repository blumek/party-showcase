package dev.blumek.party.parties.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class RoleTest {

    @Test
    void buildsFromARawName() {
        var actualRole = Role.named("Supplier");

        thenNameValueIs(actualRole, "Supplier");
    }

    private void thenNameValueIs(final Role role, final String expected) {
        assertThat(role.name().value()).isEqualTo(expected);
    }

    @Test
    void treatsRolesWithTheSameNameAsEqual() {
        var actualRole = Role.named("Supplier");

        thenRoleEquals(actualRole, Role.named("Supplier"));
    }

    private void thenRoleEquals(final Role role, final Role expected) {
        assertThat(role).isEqualTo(expected);
    }

    @Test
    void rejectsANullName() {
        var actualThrown = catchThrowable(() -> new Role(null));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
