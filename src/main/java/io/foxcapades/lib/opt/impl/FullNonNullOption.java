package io.foxcapades.lib.opt.impl;

import io.foxcapades.lib.opt.NonNullOption;
import io.foxcapades.lib.opt.NullableOption;
import io.foxcapades.lib.opt.Opt;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Non-Empty Non-Null Option.
 * <p>
 * Non singleton implementation of {@link NonNullOption} that must wrap a non
 * {@code null} value.
 *
 * @param <T> Type of the wrapped value.
 */
public class FullNonNullOption<T> extends FullOption<T, NonNullOption<T>> implements NonNullOption<T> {

  /**
   * Constructs a new non-empty option wrapping a non-null value.
   *
   * @param value Value to wrap.
   *
   * @throws NullPointerException if the given value is {@code null}.
   */
  public FullNonNullOption(@NotNull T value) {
    super(Objects.requireNonNull(value));
  }

  @Override
  public @NotNull <R> NonNullOption<R> map(@NotNull Function<? super T, ? extends R> fn) {
    final var val = fn.apply(value);
    return val == null ? Opt.standard().newNonNull() : Opt.standard().newNonNull(val);
  }

  @Override
  public @NotNull <R> NonNullOption<R> map(
    @NotNull Function<? super T, ? extends R> ifPresent,
    @NotNull Supplier<? extends R> ignored
  ) {
    Objects.requireNonNull(ignored);
    final var val = ifPresent.apply(value);
    return val == null ? Opt.standard().newNonNull() : Opt.standard().newNonNull(val);
  }

  @Override
  public @NotNull NonNullOption<T> filter(@NotNull Predicate<? super T> fn) {
    return fn.test(value) ? this : Opt.standard().newNonNull();
  }

  @Override
  public @NotNull NullableOption<T> toNullable() {
    return Opt.standard().newNullable(value);
  }

  @Override
  public @NotNull NullableOption<T> toNullable(boolean emptyToNull) {
    return Opt.standard().newNullable(value);
  }
}
