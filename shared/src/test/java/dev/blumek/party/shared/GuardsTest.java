package dev.blumek.party.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class GuardsTest {

    @Test
    void requireDoesNothingWhenTheConditionHolds() {
        var actualThrown = catchThrowable(() -> Guards.require(true, "should not throw"));

        thenNothingIsThrown(actualThrown);
    }

    private void thenNothingIsThrown(final Throwable thrown) {
        assertThat(thrown).isNull();
    }

    @Test
    void requireReportsTheMessageWhenTheConditionFails() {
        var actualThrown = catchThrowable(() -> Guards.require(false, "bad state"));

        thenIllegalArgumentIsThrownWithMessage(actualThrown, "bad state");
    }

    private void thenIllegalArgumentIsThrownWithMessage(final Throwable thrown, final String message) {
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireTextReturnsTheValueWhenTextIsPresent() {
        var actualText = Guards.requireText("abc", "blank");

        thenTextIs(actualText, "abc");
    }

    private void thenTextIs(final String actual, final String expected) {
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void requireTextRejectsNullText() {
        var actualThrown = catchThrowable(() -> Guards.requireText(null, "blank"));

        thenIllegalArgumentIsThrown(actualThrown);
    }

    private void thenIllegalArgumentIsThrown(final Throwable thrown) {
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void requireTextRejectsBlankText() {
        var actualThrown = catchThrowable(() -> Guards.requireText("   ", "blank"));

        thenIllegalArgumentIsThrown(actualThrown);
    }
}
