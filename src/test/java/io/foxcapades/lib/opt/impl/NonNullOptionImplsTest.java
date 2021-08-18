package io.foxcapades.lib.opt.impl;

import io.foxcapades.lib.opt.NonNullOption;
import io.foxcapades.lib.opt.NonNullOptionContractTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.stream.Stream;

@DisplayName("Non-Null Option Implementations")
public class NonNullOptionImplsTest extends NonNullOptionContractTest {
  @Override
  protected <T> List<? extends NonNullOption<T>> fullOptions(T value) {
    return List.of(new FullNonNullOption<>(value));
  }

  @Override
  protected @NotNull <T> NonNullOption<T> emptyOption() {
    return EmptyNonNull.instance();
  }

  @Override
  protected @NotNull <T> NonNullOption<T> fullOption(T value) {
    return new FullNonNullOption<>(value);
  }

  @Override
  protected <T> Stream<? extends NonNullOption<T>> allOptionStream(T value) {
    return allOptions(value).stream();
  }

  @Override
  protected Stream<? extends NonNullOption<Object>> fullOptions() {
    return inputStream().map(FullNonNullOption::new);
  }

  @Override
  protected <I> List<? extends NonNullOption<I>> emptyOptions() {
    return List.of(EmptyNonNull.instance());
  }

  @Override
  protected <T> List<? extends NonNullOption<T>> allOptions(T value) {
    return List.of(new FullNonNullOption<>(value), EmptyNonNull.instance());
  }
}