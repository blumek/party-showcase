package dev.blumek.party.shared;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultTest {

    private static final int VALUE = 21;
    private static final String ERROR = "boom";

    @Test
    void successIsClassifiedAsSuccess() {
        var actualResult = givenSuccess();

        thenClassifiedAsSuccess(actualResult);
    }

    private Result<String, Integer> givenSuccess() {
        return Result.success(VALUE);
    }

    private void thenClassifiedAsSuccess(final Result<String, Integer> result) {
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
    }

    @Test
    void failureIsClassifiedAsFailure() {
        var actualResult = givenFailure();

        thenClassifiedAsFailure(actualResult);
    }

    private Result<String, Integer> givenFailure() {
        return Result.failure(ERROR);
    }

    private void thenClassifiedAsFailure(final Result<String, Integer> result) {
        assertThat(result.isFailure()).isTrue();
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void foldOverASuccessTakesTheSuccessBranch() {
        var actualFolded = givenSuccess().fold(error -> "error", value -> "value-" + value);

        thenStringIs(actualFolded, "value-21");
    }

    private void thenStringIs(final String actual, final String expected) {
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void foldOverAFailureTakesTheFailureBranch() {
        var actualFolded = givenFailure().fold(error -> "error-" + error, value -> "value");

        thenStringIs(actualFolded, "error-boom");
    }

    @Test
    void mapTransformsTheValueOfASuccess() {
        var actualResult = givenSuccess().map(value -> value * 2);

        thenSuccessValueIs(actualResult, 42);
    }

    private void thenSuccessValueIs(final Result<String, Integer> result, final int expected) {
        final int actualValue = result.fold(error -> -1, value -> value);
        assertThat(actualValue).isEqualTo(expected);
    }

    @Test
    void mapLeavesAFailureUntouched() {
        var actualResult = givenFailure().map(value -> value * 2);

        thenFailureErrorIs(actualResult, ERROR);
    }

    private void thenFailureErrorIs(final Result<String, Integer> result, final String expected) {
        final String actualError = result.fold(error -> error, value -> "no-error");
        assertThat(actualError).isEqualTo(expected);
    }

    @Test
    void flatMapChainsIntoTheNextResult() {
        var actualResult = givenSuccess().flatMap(value -> Result.success(value + 1));

        thenSuccessValueIs(actualResult, 22);
    }

    @Test
    void flatMapShortCircuitsOnAFailure() {
        var actualResult = givenFailure().flatMap(value -> Result.success(value + 1));

        thenFailureErrorIs(actualResult, ERROR);
    }

    @Test
    void onSuccessRunsItsActionForASuccess() {
        var recordedActions = new ArrayList<String>();

        givenSuccess().onSuccess(value -> recordedActions.add("success"));

        thenRecordedActionsAre(recordedActions, "success");
    }

    private void thenRecordedActionsAre(final List<String> recordedActions, final String... expected) {
        assertThat(recordedActions).containsExactly(expected);
    }

    @Test
    void onSuccessSkipsItsActionForAFailure() {
        var recordedActions = new ArrayList<String>();

        givenFailure().onSuccess(value -> recordedActions.add("success"));

        thenNoRecordedActions(recordedActions);
    }

    private void thenNoRecordedActions(final List<String> recordedActions) {
        assertThat(recordedActions).isEmpty();
    }

    @Test
    void onFailureRunsItsActionForAFailure() {
        var recordedActions = new ArrayList<String>();

        givenFailure().onFailure(error -> recordedActions.add("failure"));

        thenRecordedActionsAre(recordedActions, "failure");
    }

    @Test
    void onFailureSkipsItsActionForASuccess() {
        var recordedActions = new ArrayList<String>();

        givenSuccess().onFailure(error -> recordedActions.add("failure"));

        thenNoRecordedActions(recordedActions);
    }

    @Test
    void orElseReturnsTheValueForASuccess() {
        final int actualValue = givenSuccess().orElse(0);

        thenIntIs(actualValue, VALUE);
    }

    private void thenIntIs(final int actual, final int expected) {
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void orElseReturnsTheFallbackForAFailure() {
        final int actualValue = givenFailure().orElse(0);

        thenIntIs(actualValue, 0);
    }
}
