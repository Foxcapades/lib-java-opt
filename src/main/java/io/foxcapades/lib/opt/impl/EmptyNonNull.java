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
 * Singleton type representing an empty option.
 *
 * @param <T> Generic type of this option.
 */
public record EmptyNonNull<T>() implements NonNullOption<T> {
  @SuppressWarnings("rawtypes")
  private static final NonNullOption instance = new EmptyNonNull();

  /**
   * Returns the singleton {@code EmptyNonNull} instance.
   *
   * @param <T> Generic type of the returned option.
   *
   * @return The singleton {@code EmptyNonNull} instance.
   */
  @SuppressWarnings("unchecked")
  public static <T> NonNullOption<T> instance() {
    return instance;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////


  @Override
  public boolean isPresent() {
    return false;
  }

  @Override
  public boolean isEmpty()   {
    return true;
  }

  @Override
  public @NotNull T unwrap() throws UnwrapException {
    throw new UnwrapException(this);
  }

  @Override
  public @Nullable T or(@Nullable T other) {
    return other;
  }

  @Override
  public @Nullable T orGet(@NotNull Supplier<? extends T> fn) {
    return fn.get();
  }

  @Override
  public <E extends Throwable> T orThrow(@NotNull E err) throws E {
    throw Objects.requireNonNull(err);
  }

  @Override
  public <E extends Throwable> T orElseThrow(@NotNull Supplier<? extends E> fn) throws E {
    throw Objects.requireNonNull(fn.get());
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull <R> NonNullOption<R> map(@NotNull Function<? super T, ? extends R> fn) {
    Objects.requireNonNull(fn);
    return (NonNullOption<R>) this;
  }

  @Override
  public @NotNull <R> NonNullOption<R> map(@NotNull Function<? super T, ? extends R> ifPresent, @NotNull Supplier<? extends R> ifEmpty) {
    Objects.requireNonNull(ifPresent);
    return Opt.standard().newNonNullOfNullable(ifEmpty.get());
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull <R> Option<R> flatMap(@NotNull Function<? super T, ? extends Option<? extends R>> fn) {
    Objects.requireNonNull(fn);
    return (Option<R>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull <R> Option<R> flatMap(@NotNull Function<? super T, ? extends Option<? extends R>> ifPresent, @NotNull Supplier<? extends Option<? extends R>> ifEmpty) {
    Objects.requireNonNull(ifPresent);
    return (Option<R>) Objects.requireNonNull(ifEmpty.get());
  }

  @Override
  public @NotNull Stream<T> stream() {
    return Stream.empty();
  }

  @Override
  public @NotNull NonNullOption<T> ifPresent(@NotNull Consumer<? super T> fn) {
    Objects.requireNonNull(fn);
    return this;
  }

  @Override
  public @NotNull NonNullOption<T> ifEmpty(@NotNull Runnable fn) {
    fn.run();
    return this;
  }

  @Override
  public @NotNull NonNullOption<T> with(@NotNull Consumer<? super T> ifPresent, @NotNull Runnable ifEmpty) {
    Objects.requireNonNull(ifPresent);
    ifEmpty.run();
    return this;
  }

  @Override
  public @NotNull NonNullOption<T> filter(@NotNull Predicate<? super T> fn) {
    Objects.requireNonNull(fn);
    return this;
  }

  @Override
  public boolean valueEquals(@Nullable Object value) {
    return false;
  }

  @Override
  public @NotNull NullableOption<T> toNullable() {
    return EmptyNullable.instance();
  }

  @Override
  public @NotNull NullableOption<T> toNullable(boolean emptyToNull) {
    return emptyToNull ? NullOption.instance() : EmptyNullable.instance();
  }

  @Override
  public @NotNull Option<T> orOption(@NotNull Option<T> other) {
    return Objects.requireNonNull(other);
  }

  @Override
  public @NotNull Option<T> orOption(@NotNull Supplier<Option<T>> supplier) {
    return Objects.requireNonNull(supplier.get());
  }
}
