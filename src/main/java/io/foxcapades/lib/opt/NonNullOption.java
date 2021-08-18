package io.foxcapades.lib.opt;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Non-Null Option
 * <p>
 * Option type that does not permit {@code null} values.
 *
 * @param <T> Type of the wrapped value.
 */
public interface NonNullOption<T> extends Option<T> {
  /**
   * Tests whether this {@code Option} contains a value.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @return {@code true} if this {@code Option} contains a value, otherwise
   * {@code false}.
   */
  @Override
  @Contract(pure = true)
  boolean isPresent();

  /**
   * Tests whether this {@code Option} does not contain a value.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @return {@code true} if this {@code Option} does not contain a value,
   * otherwise {@code false}.
   */
  @Override
  @Contract(pure = true)
  boolean isEmpty();

  /**
   * Attempts to unwrap this {@code Option}'s value.  If this option is empty,
   * this method throws an {@code UnwrapException}.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @return The value wrapped by this {@code Option}.
   *
   * @throws UnwrapException If this method is called on an empty
   *                         {@code Option}.
   */
  @NotNull
  @Override
  T unwrap() throws UnwrapException;

  /**
   * Returns either the value wrapped by this {@code Option}, if it is not
   * empty, or the given value if this {@code Option} is empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @param other Fallback value to return if this {@code Option} is empty.
   *              <p>
   *              This argument may be {@code null}.
   *
   * @return Either the value wrapped by this {@code Option}, or the value
   * of {@code other}.
   */
  @Override
  @Nullable
  @Contract(pure = true)
  T or(@Nullable T other);

  /**
   * Returns either the value wrapped by this {@code Option}, if it is not
   * empty, or the value returned by the given {@code Supplier}.
   * <p>
   * The given {@code Supplier} will not be called if this {@code Option} is
   * not empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @param fn {@code Supplier} for the fallback value.
   *           <p>
   *           <b>This argument must not be null.</b>
   *           <p>
   *           The value returned by the given {@code Supplier} may be null.
   *
   * @return Either the value wrapped by this {@code Option}, or the value
   * returned by the given {@code Supplier}.
   *
   * @throws NullPointerException if the given {@code Supplier} is null.
   */
  @Override
  @Nullable
  @Contract(pure = true)
  T orGet(@NotNull Supplier<? extends T> fn);

  /**
   * Returns the value wrapped by this {@code Option} if it is not empty,
   * otherwise throws the given {@code Exception} value.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @param err {@code Exception} to throw if this {@code Option} is empty.
   *            <p>
   *            <b>This argument must not be null.</b>
   * @param <E> Type of the {@code Exception} value that will be thrown if this
   *            {@code Option} is empty.
   *
   * @return The value wrapped by this {@code Option}.  Individual
   * implementations decide whether {@code null} values may be wrapped and
   * returned.
   *
   * @throws E                    Thrown if this {@code Option} is empty.
   * @throws NullPointerException If the given {@code Exception} is
   *                              {@code null}.
   */
  @Override
  @Contract(pure = true)
  <E extends Throwable> T orThrow(@NotNull E err) throws E;

  /**
   * Returns the value wrapped by this {@code Option} if it is not empty,
   * otherwise throws the {@code Exception} returned by the given
   * {@code Supplier}.
   * <p>
   * The given {@code Supplier} will not be called if this {@code Option} is not
   * empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @param <E> Type of the exception that will be returned by the given
   *            {@code Supplier} and thrown.
   * @param fn  {@code Supplier} for the {@code Exception} to throw if this
   *            {@code Option} is empty.
   *            <p>
   *            <b>This argument must not be {@code null}.</b>
   *            <p>
   *            <b>This {@code Supplier} must not return {@code null}.</b>
   *
   * @return The value wrapped by this {@code Option}.
   *
   * @throws E                    If this {@code Option} is empty.
   * @throws NullPointerException if the given {@code Supplier} is {@code null}
   *                              or if it returns {@code null}.
   */
  @Override
  @Contract(pure = true)
  <E extends Throwable> T orElseThrow(@NotNull Supplier<? extends E> fn) throws E;

  /**
   * Calls the given {@code Function} on the value wrapped by this
   * {@code Option} if and only if this {@code Option} is not empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   * <p>
   * If the given function returns {@code null} an empty {@code Option} will be
   * returned.  If the given function does not return {@code null} a new
   * {@code Option} will be returned wrapping the result value of the call
   * to the given {@code Function}.
   *
   * @param <R> The return type of the given mapping {@code Function}.
   * @param fn  Function to call on the value wrapped by this {@code Option}.
   *            <p>
   *            <b>This argument must not be {@code null}</b>
   *
   * @return A new {@code Option} of generic type {@code R}.  This
   * {@code Option} will be empty if the return value of {@code fn} was
   * {@code null}, otherwise the returned {@code Option} will be wrapping the
   * value returned by the given {@code Function}.
   *
   * @throws NullPointerException If the given {@code Function} is {@code null}.
   */
  @Override
  @Contract(pure = true)
  @NotNull <R> NonNullOption<R> map(@NotNull Function<? super T, ? extends R> fn);

  /**
   * Calls the given {@code Function} {@code ifPresent} if this {@code Option}
   * is not empty, otherwise calls the given {@code Supplier} {@code ifEmpty}.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @param <R>       Generic type of the returned {@code Option}.
   * @param ifPresent Mapping function to call on the value wrapped by this
   *                  {@code Option}.
   *                  <p>
   *                  <b>This argument must not be {@code null}.</b>
   *                  <p>
   *                  This function will only be called if this {@code Option}
   *                  is not empty.
   * @param ifEmpty   Value supplier to call when this {@code Option} is empty.
   *                  <p>
   *                  <b>This argument must not be {@code null}.</b>
   *
   * @return A new {@code Option} of generic type {@code R}.
   *
   * @throws NullPointerException If either of the given functions are
   *                              {@code null}.
   */
  @Override
  @NotNull
  @Contract(pure = true)
  <R> NonNullOption<R> map(
    @NotNull Function<? super T, ? extends R> ifPresent,
    @NotNull Supplier<? extends R> ifEmpty
  );

  /**
   * Calls the given {@code Function} if this {@code Option} is not empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @param <R> Generic type of the returned {@code Option}.
   * @param fn  Mapping function to call if this {@code Option} is not empty.
   *            <p>
   *            <b>This argument must not be null.</b>
   *            <p>
   *            <b>This function must not return null</b>
   *            <p>
   *            This function will not be called if this {@code Option} is
   *            empty.
   *
   * @return A new {@code Option} of generic type {@code R}.
   *
   * @throws NullPointerException If the given function is {@code null} or if
   *                              the given function returns {@code null}.
   */
  @NotNull
  @Override
  @Contract(pure = true)
  <R> Option<R> flatMap(@NotNull Function<? super T, ? extends Option<? extends R>> fn);

  /**
   * Calls the given mapping {@code Function} {@code ifPresent} if this
   * {@code Option} is not empty, otherwise calls the given {@code Supplier}
   * {@code ifEmpty}.
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @param <R>       Generic type for the returned {@code Option}.
   * @param ifPresent Mapping function to call if this {@code Option} is not
   *                  empty.
   *                  <p>
   *                  <b>This argument must not be {@code null}.</b>
   *                  <p>
   *                  <b>This function must not return {@code null}.</b>
   * @param ifEmpty   Supplier function to call if this {@code Option} is empty.
   *                  <p>
   *                  <b>This argument must not be null.</b>
   *                  <p>
   *                  <b>This value must not return null.</b>
   *
   * @return A new {@code Option} of generic type {@code R}.
   *
   * @throws NullPointerException If {@code ifPresent} is {@code null},
   *                              {@code ifEmpty} is {@code null}, or if the
   *                              called function returns {@code null}.
   */
  @Override
  @NotNull
  @Contract(pure = true)
  <R> Option<R> flatMap(
    @NotNull Function<? super T, ? extends Option<? extends R>> ifPresent,
    @NotNull Supplier<? extends Option<? extends R>> ifEmpty
  );

  /**
   * Creates a new {@code Stream} containing either one value, if this
   * {@code Option} is not empty, or containing zero values if this
   * {@code Option} is empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @return A new {@code Stream} that may contain the value wrapped by this
   * {@code Option}, if such a value exists, otherwise contains no values.
   */
  @Override
  @NotNull
  @Contract(pure = true)
  Stream<T> stream();

  /**
   * Executes the given {@code Consumer} with the value wrapped by this
   * {@code Option} if this {@code Option} is not empty.
   * <p>
   * The given function will not be called if this {@code Option} is empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @param fn {@code Consumer} to call on the wrapped value if this
   *           {@code Option} is not empty.
   *           <p>
   *           <b>This argument must not be null.</b>
   *
   * @return This {@code Option} instance.
   *
   * @throws NullPointerException If the given {@code Consumer} value is
   *                              {@code null}.
   */
  @NotNull
  @Override
  @Contract(value = "_ -> this", pure = true)
  NonNullOption<T> ifPresent(@NotNull Consumer<? super T> fn);

  /**
   * Executes the given {@code Runnable} if and only if this {@code Option} is
   * empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @param fn {@code Runnable} to call if this {@code Option} is empty.
   *           <p>
   *           <b>This argument must not be null.</b>
   *
   * @return This {@code Option} instance.
   *
   * @throws NullPointerException if the given {@code Runnable} value is
   *                              {@code null}.
   */
  @Override
  @NotNull
  @Contract(value = "_ -> this", pure = true)
  NonNullOption<T> ifEmpty(@NotNull Runnable fn);

  /**
   * Executes the given {@code Consumer} {@code ifPresent} on the wrapped value
   * if this {@code Option} is not empty, otherwise calls the given
   * {@code Runnable} {@code ifEmpty}.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @param ifPresent {@code Consumer} to call with the value wrapped by this
   *                  {@code Option} if this {@code Option} is not empty.
   *                  <p>
   *                  <b>This argument must not be null.</b>
   * @param ifEmpty   {@code Runnable} to call if this {@code Option} is empty.
   *                  <p>
   *                  <b>This argument must not be null.</b>
   *
   * @return This {@code Option} instance.
   *
   * @throws NullPointerException If {@code ifPresent} is {@code null}, or if
   *                              {@code ifEmpty} is {@code null}.
   */
  @Override
  @Contract(value = "_, _ -> this", pure = true)
  @NotNull
  NonNullOption<T> with(@NotNull Consumer<? super T> ifPresent, @NotNull Runnable ifEmpty);

  /**
   * Calls the given {@code Predicate} on the value wrapped by this
   * {@code Option}, if this {@code Option} is not empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface do not allow wrapping
   * {@code null} values.
   *
   * @param fn {@code Predicate} to apply to the value wrapped by this
   *           {@code Option} if this {@code Option} is not empty.
   *           <p>
   *           <b>This value must not be null.</b>
   *
   * @return This {@code Option} if the given {@code Predicate} returns
   * {@code true}, otherwise an empty {@code Option}.
   */
  @Override
  @NotNull
  @Contract(value = "_ -> this", pure = true)
  NonNullOption<T> filter(@NotNull Predicate<? super T> fn);

  /**
   * Converts this {@code NonNullOption} into an instance of
   * {@code NullableOption}.
   * <p>
   * If the current {@code Option} is empty, the returned {@code Option} will
   * also be empty.
   * <p>
   * This method is equivalent to calling {@code toNullable(false)}.
   *
   * @return A {@code NullableOption} that is either empty or wrapping the same
   * value as this {@code Option}.
   */
  @NotNull
  @Contract(pure = true)
  NullableOption<T> toNullable();

  /**
   * Converts this {@code NonNullOption} into an instance of
   * {@code NullableOption}.
   * <p>
   * If the current option is not empty, the returned {@code Option} will wrap
   * the same value as this {@code Option}.
   * <p>
   * If the current option is empty, either a new {@code NullableOption}
   * wrapping {@code null} or an empty {@code Option} will be returned depending
   * on the value of {@code emptyToNull}.
   * <p>
   * If {@code emptyToNull == true && this.isEmpty()}, then a
   * {@code NullableOption} that is not-empty, wrapping the value {@code null}
   * will be returned.
   * <p>
   * If {@code emptyToNull == false && this.isEmpty()}, then an empty option
   * will be returned.
   *
   * @param emptyToNull Flag indicating whether an empty option instance should
   *                    be converted to a null wrapping option or an empty
   *                    option.
   *
   * @return A new option that will be wrapping this option's value, null, or
   * will be empty.
   */
  @NotNull
  @Contract(pure = true)
  NullableOption<T> toNullable(boolean emptyToNull);
}
