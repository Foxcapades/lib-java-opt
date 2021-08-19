package io.foxcapades.lib.opt.impl;

import io.foxcapades.lib.opt.Option;
import io.foxcapades.lib.opt.UnwrapException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Base implementation of a non-empty option.
 *
 * @param <T> Type of the wrapped value.
 * @param <I> Type of the extending class.
 */
public abstract class FullOption<T, I extends Option<T>> implements Option<T> {
  /**
   * Value wrapped by this option.
   */
  protected final T value;

  /**
   * Constructs a new option instance wrapping the given value.
   * <p>
   * This method does not check if the given input value is {@code null}, this
   * check should be performed by implementation classes.
   *
   * @param value Value to wrap.
   */
  protected FullOption(T value) {
    this.value = value;
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
  public T unwrap() throws UnwrapException {
    return value;
  }

  @Override
  public @Nullable T or(@Nullable T ignored) {
    return value;
  }

  @Override
  public @Nullable T orGet(@NotNull Supplier<? extends T> ignored) {
    Objects.requireNonNull(ignored);
    return value;
  }

  @Override
  public <E extends Throwable> T orThrow(@NotNull E ignored) {
    Objects.requireNonNull(ignored);
    return value;
  }

  @Override
  public <E extends Throwable> T orElseThrow(@NotNull Supplier<? extends E> ignored) {
    Objects.requireNonNull(ignored);
    return value;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull <R> Option<R> flatMap(
    @NotNull Function<? super T, ? extends Option<? extends R>> fn
  ) {
    return (Option<R>) Objects.requireNonNull(fn.apply(value));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> @NotNull Option<R> flatMap(
    @NotNull Function<? super T, ? extends Option<? extends R>> ifPresent,
    @NotNull Supplier<? extends Option<? extends R>> ignored
  ) {
    Objects.requireNonNull(ignored);
    return (Option<R>) Objects.requireNonNull(ifPresent.apply(value));
  }

  @Override
  public @NotNull Stream<T> stream() {
    return Stream.of(value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull I ifPresent(@NotNull Consumer<? super T> fn) {
    fn.accept(value);
    return (I) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull I ifEmpty(@NotNull Runnable ignored) {
    Objects.requireNonNull(ignored);
    return (I) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull I with(
    @NotNull Consumer<? super T> ifPresent,
    @NotNull Runnable ignored
  ) {
    Objects.requireNonNull(ignored);
    ifPresent.accept(value);
    return (I) this;
  }

  @Override
  public boolean valueEquals(@Nullable Object value) {
    return Objects.equals(this.value, value);
  }

  @Override
  public @NotNull Option<T> orOption(@NotNull Option<T> other) {
    Objects.requireNonNull(other);
    return this;
  }

  @Override
  public @NotNull Option<T> orOption(@NotNull Supplier<Option<T>> supplier) {
    Objects.requireNonNull(supplier);
    return this;
  }
}
