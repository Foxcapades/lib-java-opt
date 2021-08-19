package io.foxcapades.lib.opt;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Nullable Option
 * <p>
 * {@code NullableOption} represents and immutable wraps a single, nullable
 * value that may or may not exist.
 *
 * <h2>Nullability</h2>
 * Implementations of this interface allow wrapping {@code null} values.  This
 * means this {@code Option} type has 3 states.
 * <ul>
 *   <li>Value is present and not {@code null}.</li>
 *   <li>Value is present and {@code null}.</li>
 *   <li>Value is absent.</li>
 * </ul>
 * <p>
 * For methods on this interface that accept {@link Function} or
 * {@link Consumer} arguments, the passed functions should be prepared to handle
 * a {@code null} input value.
 *
 * @param <T> Type of the represented/wrapped value.
 */
public interface NullableOption<T> extends Option<T> {

  /**
   * Tests whether the wrapped value is {@code null}.
   * <p>
   * A return value of {@code true} means that this {@code Option} is not empty
   * and the value it wraps is {@code null}.
   * <p>
   * A return value of {@code false} may mean either that this {@code Option}
   * <i>is</i> empty, or that the wrapped value is not {@code null}.  To test
   * if this is the case, {@link #isPresent()} and/or {@link #isEmpty()} should
   * be used.
   *
   * @return {@code true} if this {@code Option} is both non-empty and is
   * wrapping a {@code null} value.  {@code false} if this option is empty, or
   * is not wrapping a {@code null} value.
   */
  @Contract(pure = true)
  boolean isNull();

  /**
   * Converts this {@code Option} to a {@code NonNullOption}.
   * <p>
   * The conversion happens as follows:
   *
   * <table>
   *   <caption>Option conversion rules.</caption>
   *   <tr>
   *     <th>This Value</th>
   *     <th>Returns</th>
   *   </tr>
   *   <tr>
   *     <td>Not-{@code null}</td>
   *     <td>Non-empty {@code Option} wrapping this {@code Option}'s value.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code null}</td>
   *     <td>Empty {@code Option}</td>
   *   </tr>
   *   <tr>
   *     <td>empty</td>
   *     <td>Empty {@code Option}</td>
   *   </tr>
   * </table>
   *
   * @return A new {@code NonNullOption} which may be empty or non-empty based
   * on the rules detailed above.
   */
  @NotNull
  @Contract(pure = true)
  NonNullOption<T> toNonNullable();

  //
  // Overrides
  //

  /**
   * Tests whether this {@code Option} contains a value.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * The return value of this method does not indicate whether the wrapped value
   * is {@code null}.  To check whether the wrapped value is {@code null}, use
   * {@link #isNull()}.
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
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * The return value of this method does not indicate whether the wrapped value
   * is {@code null}.  To check whether the wrapped value is {@code null}, use
   * {@link #isNull()}.
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
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means the value returned may e {@code null}, and in that case, no
   * {@code UnwrapException} will be thrown.
   *
   * @return The value wrapped by this {@code Option}.  This value may be
   * {@code null}.
   *
   * @throws UnwrapException If this method is called on an empty
   *                         {@code Option}.
   */
  @Override
  @Nullable
  @Contract(pure = true)
  T unwrap() throws UnwrapException;

  /**
   * Returns either the value wrapped by this {@code Option}, if it is not
   * empty, or the given value if this {@code Option} is empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means the {@code other} value will <i>not</i> be returned if this
   * {@code Option} is wrapping a {@code null} value as {@code null} is a legal
   * and expected possible value.
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
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means the given {@code Supplier} will not be called if the wrapped
   * value is {@code null} as {@code null} is a legal and expected possible
   * value.
   * <p>
   * The given {@code Supplier} should be prepared to handle a {@code null}
   * input.
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
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means the given {@code Exception} will not be thrown if the wrapped
   * value is {@code null}.  Instead, the exception will only be thrown if this
   * {@code Option} is wrapping no value whatsoever.
   *
   * @param err {@code Exception} to throw if this {@code Option} is empty.
   *            <p>
   *            <b>This argument must not be null.</b>
   * @param <E> Type of the {@code Exception} value that will be thrown if this
   *            {@code Option} is empty.
   *
   * @return The value wrapped by this {@code Option}.
   *
   * @throws E                    Thrown if this {@code Option} is empty.
   * @throws NullPointerException If the given {@code Exception} is
   *                              {@code null}.
   */
  @Override
  @Nullable
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
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means the {@code Supplier} will not be called, and no
   * {@code Exception} will be thrown if the wrapped value is {@code null}.
   * Instead, the {@code Supplier} will be called and {@code Exception} thrown
   * only if this {@code Option} is wrapping no value whatsoever.
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
  @Nullable
  @Contract(pure = true)
  <E extends Throwable> T orElseThrow(@NotNull Supplier<? extends E> fn) throws E;

  /**
   * Calls the given {@code Function} on the value wrapped by this
   * {@code Option} if and only if this {@code Option} is not empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means the given {@code Function} should be prepared to accept
   * {@code null} values.
   * <p>
   * The given {@code Function} may {@code null}.
   *
   * @param <R> The return type of the given mapping {@code Function}.
   * @param fn  Function to call on the value wrapped by this {@code Option}.
   *            <p>
   *            <b>This argument must not be {@code null}</b>
   *
   * @return A new {@code Option} of generic type {@code R}.
   *
   * @throws NullPointerException If the given {@code Function} is null.
   */
  @Override
  @Contract(pure = true)
  @NotNull <R> NullableOption<R> map(@NotNull Function<? super T, ? extends R> fn);

  /**
   * Calls the given {@code Function} {@code ifPresent} if this {@code Option}
   * is not empty, otherwise calls the given {@code Supplier} {@code ifEmpty}.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means, if this {@code Option} is wrapping a null value, it will call
   * the {@code Function} {@code ifPresent}, passing in the wrapped {@code null}
   * value.
   * In this case, {@code ifEmpty} will not be called.
   * <p>
   * The passed {@code Function} {@code ifPresent} may return {@code null}, and
   * should be prepared to accept a {@code} null input.
   * <p>
   * If the called input function returns {@code null}, the output
   * {@code Option} will be non-empty, wrapping a {@code null} value.
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
   * @throws NullPointerException If either of the given functions is null.
   */
  @NotNull
  @Override
  @Contract(pure = true)
  <R> NullableOption<R> map(
    @NotNull Function<? super T, ? extends R> ifPresent,
    @NotNull Supplier<? extends R> ifEmpty
  );

  /**
   * Calls the given {@code Function} if this {@code Option} is not empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means, if the wrapped value is {@code null}, the given
   * {@code Function} will be called, and passed the wrapped {@code null} value.
   * <p>
   * The given {@code Function} should be prepared to handle a {@code null}
   * input.
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
   * @throws NullPointerException if the given function is {@code null} or if
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
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means, if this {@code Option} is wrapping a {@code null} value, it
   * will call the {@code Function} {@code ifPresent}, passing in the wrapped
   * {@code null} value instead of calling {@code ifEmpty}.
   * <p>
   * The given {@code Function} {@code ifPresent} should be prepared to accept
   * a {@code null} input value.
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
  @NotNull
  @Override
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
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means the single value in the returned {@code Stream} for non-empty
   * {@code Option}s may be {@code null}.
   *
   * @return A new {@code Stream} that may contain the value wrapped by this
   * {@code Option}, if such a value exists, otherwise contains no values.
   */
  @NotNull
  @Override
  @Contract(pure = true)
  Stream<T> stream();

  /**
   * Executes the given {@code Consumer} with the value wrapped by this
   * {@code Option} if this {@code Option} is not empty.
   * <p>
   * The given function will not be called if this {@code Option} is empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means the given {@code Consumer} will be called if the {@code Option}
   * is wrapping a {@code null} value passing in the wrapped {@code null} value.
   * <p>
   * The given {@code Consumer} should be prepared to handle a {@code null}
   * input.
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
  NullableOption<T> ifPresent(@NotNull Consumer<? super T> fn);

  /**
   * Executes the given {@code Runnable} if and only if this {@code Option} is
   * empty.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means the given {@code Runnable} will <i>not</i> be called if this
   * {@code Option} is wrapping a {@code null} value.
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
  @Override
  @Contract(value = "_ -> this", pure = true)
  NullableOption<T> ifEmpty(@NotNull Runnable fn);

  /**
   * Executes the given {@code Runnable} if and only if this {@code Option} is
   * wrapping a {@code null} value.
   *
   * @param fn {@code Runnable} to call if this {@code Option} is wrapping
   *           {@code null}.
   *           <p>
   *           This argument must not be null.
   *
   * @return This {@code Option} instance.
   *
   * @throws NullPointerException if the given {@code Runnable} value is
   *                              {@code null}.
   *
   * @since 1.1.0
   */
  @NotNull
  @Contract(value = "_ -> this", pure = true)
  default NullableOption<T> ifNull(@NotNull Runnable fn) {
    if (isNull())
      fn.run();
    else
      Objects.requireNonNull(fn);

    return this;
  }

  /**
   * Executes the given {@code Consumer} {@code ifPresent} on the wrapped value
   * if this {@code Option} is not empty, otherwise calls the given
   * {@code Runnable} {@code ifEmpty}.
   * <p>
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means if the wrapped value is {@code null}, the given function
   * {@code ifPresent} will be called and passed the wrapped {@code null} value.
   * <p>
   * If this {@code Option} is wrapping a null value, {@code ifEmpty} will not
   * be called.
   * <p>
   * The given {@code Consumer} {@code ifPresent} should be prepared to handle
   * a {@code null} input value.
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
  @NotNull
  @Override
  @Contract(value = "_, _ -> this", pure = true)
  NullableOption<T> with(
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
   * <b>IMPORTANT</b>: Implementations of this interface allow wrapping
   * {@code null} values.
   * This means {@code Predicate} will be called if the {@code Option} is
   * wrapping a {@code null} value, and will be passed the wrapped {@code null}.
   * <p>
   * The given {@code Predicate} should be prepared to handle a {@code null}
   * input value.
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
  @Override
  @Contract(pure = true)
  NullableOption<T> filter(@NotNull Predicate<? super T> fn);
}
