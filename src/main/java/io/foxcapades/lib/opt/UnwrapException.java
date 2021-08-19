package io.foxcapades.lib.opt;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Exception representing the case where calling code attempts to unwrap an
 * empty {@link Option} instance.
 */
public class UnwrapException extends RuntimeException {
  /**
   * Source option instance for the exception.
   */
  @NotNull
  private final Option<?> option;

  /**
   * Constructs a new {@code UnwrapException} with the given source
   * {@code Option}.
   *
   * @param option Source {@code Option} from where this exception was thrown.
   *
   * @throws NullPointerException if the given value is {@code null}.
   */
  public UnwrapException(@NotNull Option<?> option) {
    super("Attempted to unwrap the value of an empty Option.");
    this.option = Objects.requireNonNull(option);
  }

  /**
   * Returns the {@code Option} instance where the exception originated.
   *
   * @return The {@code Option} instance where the exception originated.
   */
  public @NotNull Option<?> getOption() {
    return option;
  }
}
