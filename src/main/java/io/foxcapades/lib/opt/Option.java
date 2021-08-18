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
 * Option
 * <p>
 * {@code Option} represents and immutably wraps a single value that may or may
 * not exist.
 *
 * <h2>Nullability</h2>
 * The base {@code Option} type does not set any rules for whether implementing
 * types may allow {@code null} values to be wrapped.
 * <p>
 * Implementations should detail their rules regarding null values.
 * <p>
 * For these implementations there are 3 states.  Present containing a
 * non-{@code null} value, present containing a {@code null} value, and empty.
 *
 * @param <T> Type of the represented/wrapped value.
 */
public interface Option<T> {

  /**
   * Tests whether this {@code Option} contains a value.
   * <p>
   * <b>IMPORTANT</b>: Individual implementations of {@code Option} decide
   * whether a {@code null} value may be wrapped.  For those implementations, a
   * return value of {@code true} does not mean the value contained is not
   * {@code null}.  It simply means that a value is wrapped.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
   *
   * @return {@code true} if this {@code Option} contains a value, otherwise
   * {@code false}.
   */
  @Contract(pure = true)
  boolean isPresent();

  /**
   * Tests whether this {@code Option} does not contain a value.
   * <p>
   * <b>IMPORTANT</b>: Individual implementations of {@code Option} decide
   * whether a {@code null} value may be wrapped.  For those implementations, a
   * return value of {@code false} does not indicate whether the value wrapped
   * is {@code null}, instead it simply means that no value whatsoever was
   * wrapped.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
   *
   * @return {@code true} if this {@code Option} does not contain a value,
   * otherwise {@code false}.
   */
  @Contract(pure = true)
  boolean isEmpty();

  /**
   * Attempts to unwrap this {@code Option}'s value.  If this option is empty,
   * this method throws an {@code UnwrapException}.
   * <p>
   * <b>IMPORTANT</b>: Individual implementations of {@code Option} decide
   * whether a {@code null} value may be wrapped.  For those implementations,
   * this method will <i>not</i> throw an exception if the value returned is
   * {@code null}.  Instead it will only throw an exception if no value was
   * set whatsoever.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
   *
   * @return The value wrapped by this {@code Option}.  This value may be
   * {@code null} depending on whether this implementation of {@code Option}
   * allows {@code null} values.
   *
   * @throws UnwrapException If this method is called on an empty
   *                         {@code Option}.
   */
  @Contract(pure = true)
  T unwrap() throws UnwrapException;

  /**
   * Returns either the value wrapped by this {@code Option}, if it is not
   * empty, or the given value if this {@code Option} is empty.
   * <p>
   * <b>IMPORTANT</b>: Individual implementations of {@code Option} decide
   * whether a {@code null} value may be wrapped.  For those implementations,
   * the {@code other} value will <i>not</i> be returned if the {@code Option}
   * is wrapping a {@code null} value as, for those implementations,
   * {@code null} is a legal and expected possible value.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
   *
   * @param other Fallback value to return if this {@code Option} is empty.
   *              <p>
   *              This argument may be {@code null}.
   *
   * @return Either the value wrapped by this {@code Option}, or the value
   * of {@code other}.
   */
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
   * <b>IMPORTANT</b>: Individual implementations of {@code Option} decide
   * whether a {@code null} value may be wrapped.  For those implementations,
   * the {@code Supplier} will not be called if the {@code Option} is wrapping
   * a {@code null} value as, for those implementations, {@code null} is a legal
   * and expected possible value.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
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
  @Nullable
  @Contract(pure = true)
  T orGet(@NotNull Supplier<? extends T> fn);

  /**
   * Returns the value wrapped by this {@code Option} if it is not empty,
   * otherwise throws the given {@code Exception} value.
   * <p>
   * <b>IMPORTANT</b>: Individual implementations of {@code Option} decide
   * whether a {@code null} value may be wrapped.
   * For those implementations, the given {@code Exception} will not be thrown
   * if the wrapped value is {@code null}.  Instead, the exception will only
   * be thrown if this {@code Option} is wrapping no value whatsoever.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
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
   * <b>IMPORTANT</b>: Individual implementations of {@code Option} decide
   * whether a {@code null} value may be wrapped.
   * For those implementations, the {@code Supplier} will not be called, and
   * no {@code Exception} will be thrown if the wrapped value is {@code null}.
   * Instead, the {@code Supplier} will be called and {@code Exception} thrown
   * only if this {@code Option} is wrapping no value whatsoever.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
   *
   * @param <E> Type of the exception that will be returned by the given
   *            {@code Supplier} and thrown.
   *
   * @param fn  {@code Supplier} for the {@code Exception} to throw if this
   *            {@code Option} is empty.
   *            <p>
   *            <b>This argument must not be {@code null}.</b>
   *            <p>
   *            <b>This {@code Supplier} must not return {@code null}.</b>
   * @return The value wrapped by this {@code Option}.  Individual
   * implementations decide whether {@code null} values may be wrapped.
   *
   * @throws E                    If this {@code Option} is empty.
   * @throws NullPointerException if the given {@code Supplier} is {@code null}
   *                              or if it returns {@code null}.
   */
  @Contract(pure = true)
  <E extends Throwable> T orElseThrow(@NotNull Supplier<? extends E> fn) throws E;

  /**
   * Calls the given {@code Function} on the value wrapped by this
   * {@code Option} if and only if this {@code Option} is not empty.
   * <p>
   * If the given function returns {@code null} an empty {@code Option} will be
   * returned.  If the given function does not return {@code null} a new
   * {@code Option} will be returned wrapping the result value of the call
   * to the given {@code Function}.
   * <p>
   * <b>IMPORTANT</b>: Individual implementations of {@code Option} decide
   * whether a {@code null} value may be wrapped.
   * For those implementations, the given {@code Function} should be prepared
   * to accept {@code null} values.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
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
   * @throws NullPointerException If the given {@code Function} is null.
   */
  @Contract(pure = true)
  @NotNull <R> Option<R> map(@NotNull Function<? super T, ? extends R> fn);

  /**
   * Calls the given {@code Function} {@code ifPresent} if this {@code Option}
   * is not empty, otherwise calls the given {@code Supplier} {@code ifEmpty}.
   * <p>
   * <b>IMPORTANT</b>: Individual implementations of {@code Option} decide
   * whether a {@code null} value may be wrapped.
   * For those implementations, if the {@code Option} is wrapping a null value,
   * it will call the {@code Function} {@code ifPresent}, passing in the wrapped
   * {@code null} value.  This means, for those {@code Option} implementations,
   * the {@code Function} passed as {@code ifPresent} should be prepared to
   * handle {@code null} values.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
   *
   * @param <R>       Generic type of the returned {@code Option}.
   *
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
   * @return A new {@code Option} of generic type {@code R}.
   *
   * @throws NullPointerException If either of the given functions is
   *                              {@code null}.
   */
  @NotNull
  @Contract(pure = true)
  <R> Option<R> map(
    @NotNull Function<? super T, ? extends R> ifPresent,
    @NotNull Supplier<? extends R> ifEmpty
  );

  /**
   * Calls the given {@code Function} if this {@code Option} is not empty.
   * <p>
   * <b>IMPORTANT</b>: Individual implementations of {@code Option} decide
   * whether a {@code null} value may be wrapped.
   * For those implementations, if the wrapped value is {@code null}, the given
   * {@code Function} will be called, and passed the wrapped {@code null} value.
   * If the current implementation allows nulls, this {@code Function} should
   * be prepared to handle {@code null} inputs.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
   *
   * @param <R> Generic type of the returned {@code Option}.
   *
   * @param fn  Mapping function to call if this {@code Option} is not empty.
   *            <p>
   *            <b>This argument must not be null.</b>
   *            <p>
   *            <b>This function must not return null</b>
   *            <p>
   *            This function will not be called if this {@code Option} is
   *            empty.
   * @return A new {@code Option} of generic type {@code R}.
   *
   * @throws NullPointerException if the given function is {@code null} or if
   *                              the given function returns {@code null}.
   */
  @NotNull
  @Contract(pure = true)
  <R> Option<R> flatMap(@NotNull Function<? super T, ? extends Option<? extends R>> fn);

  /**
   * Calls the given mapping {@code Function} {@code ifPresent} if this
   * {@code Option} is not empty, otherwise calls the given {@code Supplier}
   * {@code ifEmpty}.
   * <b>IMPORTANT</b>: Individual implementations of {@code Option} decide
   * whether a {@code null} value may be wrapped.
   * For those implementations, if the {@code Option} is wrapping a null value,
   * it will call the {@code Function} {@code ifPresent}, passing in the wrapped
   * {@code null} value.  This means, for those {@code Option} implementations,
   * the {@code Function} passed as {@code ifPresent} should be prepared to
   * handle {@code null} values.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
   *
   * @param <R>       Generic type for the returned {@code Option}.
   *
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
   * @return A new {@code Option} of generic type {@code R}.
   *
   * @throws NullPointerException If {@code ifPresent} is {@code null},
   *                              {@code ifEmpty} is {@code null}, or if the
   *                              called function returns {@code null}.
   */
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
   * <b>IMPORTANT</b>: Individual implementations of {@code Option} decide
   * whether a {@code null} value may be wrapped.
   * This means, for those implementations, the single value in the returned
   * {@code Stream} for non-empty {@code Option}s may be {@code null}.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
   *
   * @return A new {@code Stream} that may contain the value wrapped by this
   * {@code Option}, if such a value exists, otherwise contains no values.
   */
  @NotNull
  @Contract(pure = true)
  Stream<T> stream();

  /**
   * Executes the given {@code Consumer} with the value wrapped by this
   * {@code Option} if this {@code Option} is not empty.
   * <p>
   * The given function will not be called if this {@code Option} is empty.
   * <p>
   * <b>IMPORTANT</b>: Individual {@code Option} implementations decide whether
   * a {@code null} value may be wrapped.
   * For those implementations, the given {@code Consumer} will be called if the
   * {@code Option} is wrapping a {@code null} value.  This means, for those
   * implementations, the passed {@code Consumer} should be prepared to handle a
   * {@code null} input.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
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
  @Contract(value = "_ -> this", pure = true)
  Option<T> ifPresent(@NotNull Consumer<? super T> fn);

  /**
   * Executes the given {@code Runnable} if and only if this {@code Option} is
   * empty.
   * <p>
   * <b>IMPORTANT</b>: Individual {@code Option} implementations decide whether
   * a {@code null} value may be wrapped.
   * For those implementations, if this {@code Option} is wrapping a
   * {@code null} value, the given function will not be called.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
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
  @NotNull
  @Contract(value = "_ -> this", pure = true)
  Option<T> ifEmpty(@NotNull Runnable fn);

  /**
   * Executes the given {@code Consumer} {@code ifPresent} on the wrapped value
   * if this {@code Option} is not empty, otherwise calls the given
   * {@code Runnable} {@code ifEmpty}.
   * <p>
   * <b>IMPORTANT</b>: Individual {@code Option} implementations decide whether
   * a {@code null} value may be wrapped.
   * For those implementations, if the wrapped value is {@code null}, the given
   * function {@code ifPresent} will be called and passed the wrapped
   * {@code null} value, {@code null} values are considered legal and possible
   * valid values.  This means, for those {@code Option} implementations, the
   * given {@code ifPresent} function should be prepared to handle a
   * {@code null} input.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
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
  @Contract(value = "_, _ -> this", pure = true)
  @NotNull
  Option<T> with(
    @NotNull Consumer<? super T> ifPresent,
    @NotNull Runnable ifEmpty
  );

  /**
   * Calls the given {@code Predicate} on the value wrapped by this
   * {@code Option}, if this {@code Option} is not empty.
   * <p>
   * The given {@code Predicate} will not be called if this {@code Option} is
   * empty.
   * <p>
   * If the given {@code Predicate} returns {@code true}, this {@code Option}
   * will be returned, otherwise an empty {@code Option} will be returned.
   * <p>
   * <b>IMPORTANT</b>: Individual {@code Option} implementations decide whether
   * a {@code null} value may be wrapped.
   * For those implementations, the given {@code Predicate} will be called if
   * the {@code Option} is wrapping a {@code null} value, and will be passed
   * the wrapped {@code null}.  This means, for those implementations, the given
   * {@code Predicate} should be prepared to handle a {@code null} input.
   * <p>
   * Implementers should detail their rules regarding {@code null} values.
   *
   * @param fn {@code Predicate} to apply to the value wrapped by this
   *           {@code Option} if this {@code Option} is not empty.
   *           <p>
   *           <b>This value must not be null.</b>
   *
   * @return This {@code Option} if the given {@code Predicate} returns
   * {@code true}, otherwise an empty {@code Option}.
   */
  @NotNull
  @Contract(pure = true)
  Option<T> filter(@NotNull Predicate<? super T> fn);

  /**
   * Returns whether the value wrapped by this option equals the given input
   * value.
   * <p>
   * For empty options, this method will always return {@code false}.
   *
   * @param value Value to compare against the wrapped value.
   *
   * @return Whether the given value equals the value wrapped by this option.
   * If this option is empty, this value will always be {@code false}.  If this
   * option allows {@code null} values and is wrapping a {@code null} value,
   * this method will only return {@code true} if the given input is also
   * {@code null}.
   */
  @Contract(pure = true)
  boolean valueEquals(@Nullable Object value);
}
