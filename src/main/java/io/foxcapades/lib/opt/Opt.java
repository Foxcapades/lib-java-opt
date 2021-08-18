package io.foxcapades.lib.opt;

import io.foxcapades.lib.opt.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Option factory.
 */
public class Opt {
  private static final ReadWriteLock lock            = new ReentrantReadWriteLock();
  private static       Opt           defaultInstance = new Opt();

  /**
   * Constructs a new, non-empty option wrapping the given nullable value.
   * <p>
   * This method is a static convenience wrapper around the instance method
   * {@link #newNullable(Object)}.
   *
   * @param value Nullable value to wrap.
   * @param <T>   Generic type of the input value and returned option.
   *
   * @return A new, non-empty option wrapping the given value.
   */
  @NotNull
  public static <T> NullableOption<T> nullable(@Nullable T value) {
    return standard().newNullable(value);
  }

  /**
   * Constructs a new, non-empty option wrapping the given nullable value.
   *
   * @param value Nullable value to wrap.
   * @param <T>   Generic type of the input value and returned option.
   *
   * @return A new, non-empty option wrapping the given value.
   */
  @NotNull
  public <T> NullableOption<T> newNullable(@Nullable T value) {
    return value == null ? NullOption.instance() : new FullNullableOption<>(value);
  }

  /**
   * Constructs a new, empty option.
   * <p>
   * This method is a static convenience wrapper around the instance method
   * {@link #newNullable()}.
   *
   * @param <T> Generic type of the returned option.
   *
   * @return A new, empty option.
   */
  @NotNull
  public static <T> NullableOption<T> nullable() {
    return standard().newNullable();
  }


  /**
   * Constructs a new, empty option.
   *
   * @param <T> Generic type of the returned option.
   *
   * @return A new, empty option.
   */
  @NotNull
  public <T> NullableOption<T> newNullable() {
    return EmptyNullable.instance();
  }

  /**
   * Constructs a new, non-empty option wrapping the given value.
   *
   * @param value Value to wrap.  Must not be {@code null}.
   * @param <T>   Generic type of the output option, and type of the input
   *              value.
   *
   * @return A new, non-empty option wrapping the given value.
   *
   * @throws NullPointerException if the given input value is {@code null}.
   */
  @NotNull
  public <T> NonNullOption<T> newNonNull(@NotNull T value) {
    return new FullNonNullOption<>(Objects.requireNonNull(value));
  }

  /**
   * Constructs a new, non-empty option wrapping the given value.
   * <p>
   * This method is a static convenience wrapper around the instance method
   * {@link #newNonNull(Object)}.
   *
   * @param value Value to wrap.  Must not be {@code null}.
   * @param <T>   Generic type of the output option, and type of the input
   *              value.
   *
   * @return A new, non-empty option wrapping the given value.
   *
   * @throws NullPointerException if the given input value is {@code null}.
   */
  @NotNull
  public static <T> NonNullOption<T> nonNull(@NotNull T value) {
    return standard().newNonNull(value);
  }

  /**
   * Constructs a new, empty option.
   *
   * @param <T> Generic type of the output option.
   *
   * @return A new empty option.
   */
  @NotNull
  public <T> NonNullOption<T> newNonNull() {
    return EmptyNonNull.instance();
  }

  /**
   * Constructs a new, empty option.
   * <p>
   * This method is a static convenience wrapper around the instance method
   * {@link #newNonNull()}.
   *
   * @param <T> Generic type of the output option.
   *
   * @return A new empty option.
   */
  public static <T> NonNullOption<T> nonNull() {
    return standard().newNonNull();
  }

  /**
   * Constructs a new option that is empty if the given input is {@code null},
   * or wrapping the given value if it is not {@code null}.
   *
   * @param value Input value to wrap or {@code null} for an empty option.
   * @param <T>   Generic type of the output option and type of the input value.
   *
   * @return An empty option if the input value is {@code null} or an option
   * wrapping the given value if it is not {@code null}.
   */
  @NotNull
  public <T> NonNullOption<T> newNonNullOfNullable(@Nullable T value) {
    return value == null
      ? EmptyNonNull.instance()
      : new FullNonNullOption<>(value);
  }

  /**
   * Constructs a new option that is empty if the given input is {@code null},
   * or wrapping the given value if it is not {@code null}.
   * <p>
   * This method is a static convenience wrapper around the instance method
   * {@link #newNonNullOfNullable(Object)}.
   *
   * @param value Input value to wrap or {@code null} for an empty option.
   * @param <T>   Generic type of the output option and type of the input value.
   *
   * @return An empty option if the input value is {@code null} or an option
   * wrapping the given value if it is not {@code null}.
   */
  @NotNull
  public static <T> NonNullOption<T> nonNullOfNullable(@Nullable T value) {
    return standard().newNonNullOfNullable(value);
  }

  /**
   * Returns the currently set standard/singleton instance that will be used by
   * the static convenience methods.
   *
   * @return The currently set standard/singleton instance of this class.
   */
  @NotNull
  public static Opt standard() {
    lock.readLock().lock();
    var out = defaultInstance;
    lock.readLock().unlock();
    return out;
  }

  /**
   * Sets the standard/singleton instance that will be used by the static
   * convenience methods.
   *
   * @param inst New opt instance to use as the standard implementation.
   *
   * @throws NullPointerException if the input value is {@code null}.
   */
  public static void setStandardInstance(@NotNull Opt inst) {
    lock.writeLock().lock();
    defaultInstance = Objects.requireNonNull(inst);
    lock.writeLock().unlock();
  }
}
