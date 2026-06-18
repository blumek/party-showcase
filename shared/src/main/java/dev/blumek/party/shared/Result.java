package dev.blumek.party.shared;

import java.util.function.Consumer;
import java.util.function.Function;

public sealed interface Result<E, T> permits Result.Success, Result.Failure {

    static <E, T> Result<E, T> success(T value) {
        return new Success<>(value);
    }

    static <E, T> Result<E, T> failure(E error) {
        return new Failure<>(error);
    }

    boolean isSuccess();

    default boolean isFailure() {
        return !isSuccess();
    }

    <U> Result<E, U> map(Function<? super T, ? extends U> mapper);

    <U> Result<E, U> flatMap(Function<? super T, Result<E, U>> mapper);

    Result<E, T> onSuccess(Consumer<? super T> action);

    Result<E, T> onFailure(Consumer<? super E> action);

    <U> U fold(Function<? super E, ? extends U> onFailure, Function<? super T, ? extends U> onSuccess);

    T orElse(T fallback);

    record Success<E, T>(T value) implements Result<E, T> {

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public <U> Result<E, U> map(final Function<? super T, ? extends U> mapper) {
            return new Success<>(mapper.apply(value));
        }

        @Override
        public <U> Result<E, U> flatMap(final Function<? super T, Result<E, U>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public Result<E, T> onSuccess(final Consumer<? super T> action) {
            action.accept(value);
            return this;
        }

        @Override
        public Result<E, T> onFailure(final Consumer<? super E> action) {
            return this;
        }

        @Override
        public <U> U fold(final Function<? super E, ? extends U> onFailure, final Function<? super T, ? extends U> onSuccess) {
            return onSuccess.apply(value);
        }

        @Override
        public T orElse(final T fallback) {
            return value;
        }
    }

    record Failure<E, T>(E error) implements Result<E, T> {

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Result<E, U> map(final Function<? super T, ? extends U> mapper) {
            return (Result<E, U>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Result<E, U> flatMap(final Function<? super T, Result<E, U>> mapper) {
            return (Result<E, U>) this;
        }

        @Override
        public Result<E, T> onSuccess(final Consumer<? super T> action) {
            return this;
        }

        @Override
        public Result<E, T> onFailure(final Consumer<? super E> action) {
            action.accept(error);
            return this;
        }

        @Override
        public <U> U fold(final Function<? super E, ? extends U> onFailure, final Function<? super T, ? extends U> onSuccess) {
            return onFailure.apply(error);
        }

        @Override
        public T orElse(final T fallback) {
            return fallback;
        }
    }
}
