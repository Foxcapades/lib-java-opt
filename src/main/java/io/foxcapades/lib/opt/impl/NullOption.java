package io.foxcapades.lib.opt.impl;

import io.foxcapades.lib.opt.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Singleton type representing a {@code null} option.
 *
 * @param <T> Generic type of this option.
 */
public record NullOption<T>() implements NullableOption<T> {
  @SuppressWarnings("rawtypes")
  private static final NullableOption Instance = new NullOption();

  @Override
  public boolean isNull() {
    return true;
  }

  @Override
  public boolean isPresent() {
    return true;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public @Nullable T unwrap() throws UnwrapException {
    return null;
  }

  @Override
  public @Nullable T or(@Nullable T other) {
    return null;
  }

  @Override
  public @Nullable T orGet(@NotNull Supplier<? extends T> fn) {
    Objects.requireNonNull(fn);
    return null;
  }

  @Override
  public <E extends Throwable> @Nullable T orThrow(@NotNull E err) {
    Objects.requireNonNull(err);
    return null;
  }

  @Override
  public <E extends Throwable> @Nullable T orElseThrow(@NotNull Supplier<? extends E> fn) {
    Objects.requireNonNull(fn);
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull <R> NullableOption<R> map(@NotNull Function<? super T, ? extends R> fn) {
    final var val = fn.apply(null);
    return val == null ? (NullableOption<R>) this : Opt.standard().newNullable(val);
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull <R> NullableOption<R> map(
    @NotNull Function<? super T, ? extends R> ifPresent,
    @NotNull Supplier<? extends R> ignored
  ) {
    Objects.requireNonNull(ignored);

    final var val = ifPresent.apply(null);

    return val == null ? (NullableOption<R>) this : Opt.standard().newNullable(val);
  }

  @NotNull
  @Override
  @SuppressWarnings("unchecked")
  public <R> Option<R> flatMap(@NotNull Function<? super T, ? extends Option<? extends R>> fn) {
    return (Option<R>) Objects.requireNonNull(fn.apply(null));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> @NotNull Option<R> flatMap(
    @NotNull Function<? super T, ? extends Option<? extends R>> ifPresent,
    @NotNull Supplier<? extends Option<? extends R>> ignored
  ) {
    Objects.requireNonNull(ignored);
    return (Option<R>) Objects.requireNonNull(ifPresent.apply(null));
  }

  @Override
  public @NotNull Stream<T> stream() {
    return Stream.of((T) null);
  }

  @Override
  public @NotNull NullableOption<T> ifPresent(@NotNull Consumer<? super T> fn) {
    fn.accept(null);
    return this;
  }

  @Override
  public @NotNull NullableOption<T> ifEmpty(@NotNull Runnable ignored) {
    Objects.requireNonNull(ignored);
    return this;
  }

  @Override
  public @NotNull NullableOption<T> with(
    @NotNull Consumer<? super T> ifPresent,
    @NotNull Runnable ignored
  ) {
    Objects.requireNonNull(ignored);

    ifPresent.accept(null);

    return this;
  }

  @Override
  public @NotNull NullableOption<T> filter(@NotNull Predicate<? super T> fn) {
    return fn.test(null) ? this : Opt.standard().newNullable();
  }

  @NotNull
  @Override
  public NonNullOption<T> toNonNullable() {
    return Opt.standard().newNonNull();
  }

  /**
   * Returns the singleton {@code NullOption} instance.
   *
   * @param <T> Generic type of the returned option.
   *
   * @return The singleton {@code NullOption} instance.
   */
  @SuppressWarnings("unchecked")
  public static <T> @NotNull NullableOption<T> instance() {
    return (NullableOption<T>) Instance;
  }

  @Override
  public boolean valueEquals(@Nullable Object value) {
    return value == null;
  }
}
