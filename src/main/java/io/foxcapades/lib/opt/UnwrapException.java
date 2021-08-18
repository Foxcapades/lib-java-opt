package io.foxcapades.lib.opt;

import org.jetbrains.annotations.NotNull;

/**
 * Exception representing the case where calling code attempts to unwrap an
 * empty {@link Option} instance.
 */
public class UnwrapException extends RuntimeException {
  /**
   * Source option instance for the exception.
   */
  private final Option<?> option;

  public UnwrapException(@NotNull Option<?> option) {
    super("Attempted to unwrap the value of an empty Option.");
    this.option = option;
  }

  /**
   * Returns the {@code Option} instance where the exception originated.
   *
   * @return The {@code Option} instance where the exception originated.
   */
  public Option<?> getOption() {
    return option;
  }
}
