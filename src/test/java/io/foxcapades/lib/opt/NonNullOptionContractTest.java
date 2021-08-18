package io.foxcapades.lib.opt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public abstract class NonNullOptionContractTest extends OptionContractTest {
  @Override
  protected abstract <T> NonNullOption<T> emptyOption();

  @Override
  protected abstract <T> NonNullOption<T> fullOption(T value);

  @Override
  protected abstract  <T> Stream<? extends NonNullOption<T>> allOptionStream(T value);

  @Override
  protected abstract Stream<? extends NonNullOption<Object>> fullOptions();

  @Override
  protected abstract <I> List<? extends NonNullOption<I>> emptyOptions();

  @Override
  protected abstract <T> List<? extends NonNullOption<T>> fullOptions(T value);

  //
  //
  // CONTRACT TESTS
  //
  //

  @Nested
  @DisplayName("#map(Function<T, R>")
  public class Map1 extends OptionContractTest.Map1 {

    @Nested
    @DisplayName(FNEO)
    public class Full extends OptionContractTest.Map1.Full {

      @Test
      @DisplayName("returns an empty option when the given mapping function returns null.")
      public void t3() {
        assertTrue(emptyOption().map(x -> 2).isEmpty());
      }
    }
  }

  @Nested
  @DisplayName("#toNullable()")
  class ToNullable1 {

    @Nested
    @DisplayName(FNEO)
    class Full {

      @Test
      @DisplayName("returns a non-empty option wrapping the same value.")
      void t1() {

        fullOptions().forEach(o -> assertSame(o.unwrap(), o.toNullable().unwrap()));
      }
    }

    @Nested
    @DisplayName(FEO)
    class Empty {

      @Test
      @DisplayName("returns an empty option.")
      void t1() {
        for (var opt : emptyOptions())
          assertTrue(opt.toNullable().isEmpty());
      }
    }
  }

  @Nested
  @DisplayName("#toNullable(boolean)")
  class ToNullable2 {

    @Nested
    @DisplayName(FNEO)
    class Full {

      @Nested
      @DisplayName("when given an input of true")
      class True {

        @Test
        @DisplayName("returns a non-empty option wrapping the same value.")
        void t1() {
          for (var opt : fullOptions(99))
            assertSame(opt.unwrap(), opt.toNullable(true).unwrap(), opt.getClass().getSimpleName());
        }
      }

      @Nested
      @DisplayName("when given an input of false")
      class False {

        @Test
        @DisplayName("returns a non-empty option wrapping the same value.")
        void t1() {
          for (var opt : fullOptions(99))
            assertSame(opt.unwrap(), opt.toNullable(false).unwrap(), opt.getClass().getSimpleName());
        }
      }
    }

    @Nested
    @DisplayName(FEO)
    class Empty {

      @Nested
      @DisplayName("when given an input of true")
      class True {

        @Test
        @DisplayName("returns a non-empty option wrapping null.")
        void t1() {
          for (var opt : emptyOptions())
            assertNull(opt.toNullable(true).unwrap(), opt.getClass().getSimpleName());
        }
      }

      @Nested
      @DisplayName("when given an input of false")
      class False {

        @Test
        @DisplayName("returns an empty option.")
        void t1() {
          for (var opt : emptyOptions())
            assertTrue(opt.toNullable(false).isEmpty(), opt.getClass().getSimpleName());
        }
      }
    }
  }
}
