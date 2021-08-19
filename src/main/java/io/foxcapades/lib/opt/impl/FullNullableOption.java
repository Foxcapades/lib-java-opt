package io.foxcapades.lib.opt.impl;

import io.foxcapades.lib.opt.NonNullOption;
import io.foxcapades.lib.opt.NullableOption;
import io.foxcapades.lib.opt.Opt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Non-Empty Nullable Option
 * <p>
 * Non-singleton implementation of {@code NullableOption} that must wrap a value
 * which may be {@code null}.
 *
 * @param <T> Type of the wrapped value.
 */
public class FullNullableOption<T> extends FullOption<T, NullableOption<T>> implements NullableOption<T> {

  /**
   * Constructs a new option instance wrapping the given nullable value.
   *
   * @param value Value to wrap.
   */
  public FullNullableOption(@Nullable T value) {
    super(value);
  }

  @Override
  public boolean isNull() {
    return value == null;
  }

  @Override
  public @NotNull <R> NullableOption<R> map(@NotNull Function<? super T, ? extends R> fn) {
    return Opt.standard().newNullable(fn.apply(value));
  }

  @Override
  public @NotNull <R> NullableOption<R> map(
    @NotNull Function<? super T, ? extends R> ifPresent,
    @NotNull Supplier<? extends R> ignored
  ) {
    Objects.requireNonNull(ignored);
    return Opt.standard().newNullable(ifPresent.apply(value));
  }

  @Override
  public @NotNull NullableOption<T> filter(@NotNull Predicate<? super T> fn) {
    return fn.test(value) ? this : Opt.standard().newNullable();
  }

  @NotNull
  @Override
  public NonNullOption<T> toNonNullable() {
    return value == null ? Opt.standard().newNonNull() : Opt.standard().newNonNull(value);
  }

  @Override
  public @NotNull NullableOption<T> ifNull(@NotNull Runnable fn) {
    if (value == null)
      fn.run();
    else
      Objects.requireNonNull(fn);

    return this;
  }
}
