package io.foxcapades.lib.opt.impl;

import io.foxcapades.lib.opt.NullableOption;
import io.foxcapades.lib.opt.NullableOptionContractTest;
import io.foxcapades.lib.opt.Option;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.stream.Stream;

@DisplayName("Nullable Option Implementations")
public class NullableOptionImplsTest extends NullableOptionContractTest {
  @Override
  protected @NotNull <T> NullableOption<T> emptyOption() {
    return EmptyNullable.instance();
  }

  @Override
  protected @NotNull <T> NullableOption<T> fullOption(T value) {
    return value == null ? NullOption.instance() : new FullNullableOption<>(value);
  }

  @Override
  protected <T> NullableOption<T> nullOption() {
    return NullOption.instance();
  }

  @Override
  protected <T> List<? extends NullableOption<T>> allOptions(T value) {
    return List.of(
      new FullNullableOption<>(value),
      NullOption.instance(),
      EmptyNullable.instance()
    );
  }

  @Override
  protected Stream<? extends NullableOption<Object>> fullOptions() {
    return inputStream().map(FullNullableOption::new);
  }

  @Override
  protected <I> List<? extends NullableOption<I>> emptyOptions() {
    return List.of(EmptyNullable.instance());
  }

  @Override
  protected <T> Stream<? extends NullableOption<T>> allOptionStream(T value) {
    return allOptions(value).stream();
  }

  @Override
  protected <T> List<NullableOption<T>> fullOptions(T value) {
    return List.of(new FullNullableOption<>(value));
  }
}