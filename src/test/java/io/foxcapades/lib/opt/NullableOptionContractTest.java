package io.foxcapades.lib.opt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NullableOption<T>")
public abstract class NullableOptionContractTest extends OptionContractTest {
  public static final String NullText = "called on a null option";
  public static final String NotNullText = "called on a non-null option";

  @Override
  protected abstract <T> List<? extends NullableOption<T>> fullOptions(T value);

  @Override
  protected abstract <T> Stream<? extends NullableOption<T>> allOptionStream(T value);

  @Override
  protected abstract <T> List<? extends NullableOption<T>> allOptions(T value);

  @Override
  protected abstract Stream<? extends NullableOption<Object>> fullOptions();

  @Override
  protected abstract <I> List<? extends NullableOption<I>> emptyOptions();

  @Override
  protected abstract <T> NullableOption<T> emptyOption();

  @Override
  protected abstract <T> NullableOption<T> fullOption(T value);

  protected abstract <T> NullableOption<T> nullOption();

  @Override
  protected List<Object> inputList() {
    return new ArrayList<>(super.inputList()) {{ add(null); }};
  }

  @Nested
  @DisplayName("#isPresent()")
  public class IsPresent extends OptionContractTest.IsPresent {

    @Nested
    @DisplayName(NullText)
    public class Null {

      @Test
      @DisplayName("returns true.")
      public void t1() {
        var o = nullOption();

        assertTrue(o.isPresent(), o.getClass().getSimpleName());
      }
    }
  }

  @Nested
  @DisplayName("#isNull()")
  public class IsNull {

    @Nested
    @DisplayName(FNEO)
    public class Full {

      @Test
      @DisplayName("returns true if wrapping a null value.")
      public void test1() {
        var opt = nullOption();
        assertTrue(opt.isNull(), opt.getClass().getSimpleName());
      }

      @Test
      @DisplayName("returns false if wrapping a non-null value.")
      public void test2() {
        for (var opt : fullOptions("")) {
          assertFalse(opt.isNull(), opt.getClass().getSimpleName());
        }
      }
    }

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("returns false.")
      public void test1() {
        var opt = emptyOption();
        assertFalse(opt.isNull(), opt.getClass().getSimpleName());
      }
    }
  }

  @Nested
  @DisplayName("#toNonNullable()")
  public class ToNotNullable {

    @Nested
    @DisplayName(FNEO)
    public class Full {

      @Test
      @DisplayName("returns an empty option when wrapping a null value.")
      void t1() {
        var opt = nullOption();
        assertTrue(opt.toNonNullable().isEmpty(), opt.getClass().getSimpleName());
      }

      @Test
      @DisplayName("returns a non-empty option when wrapping the same value.")
      void t2() {
        var input = new Object();
        for (var opt : fullOptions(input)) {
          assertSame(input, opt.toNonNullable().unwrap(), opt.getClass().getSimpleName());
        }
      }
    }

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("returns an empty options.")
      void test1() {
        assertTrue(emptyOption().toNonNullable().isEmpty());
      }
    }
  }

  @Nested
  @DisplayName("#orThrow(E)")
  public class OrThrow1 extends OptionContractTest.OrThrow1 {

    @Test
    @DisplayName("returns null.")
    public void tt1() {
      assertNull(
        nullOption().orThrow(new RuntimeException()),
        () -> nullOption().getClass().getSimpleName()
      );
    }
  }

  @Nested
  @DisplayName("#orThrow(Supplier<E>)")
  public class OrElseThrow extends OrThrow2 {

    @Test
    @DisplayName("returns null.")
    public void tt1() {
      assertNull(
        nullOption().orElseThrow(RuntimeException::new),
        () -> nullOption().getClass().getSimpleName()
      );
    }
  }

  @Nested
  @DisplayName("#map(Function<T, R>)")
  public class Map1 extends OptionContractTest.Map1 {

    @Nested
    @DisplayName(FNEO)
    public class Full extends OptionContractTest.Map1.Full {

      @Nested
      @DisplayName("wrapping null")
      public class Null {

        @Test
        @DisplayName("passes null to the given mapping function.")
        void test1() {
          var opt = nullOption();
          var con = new S.Counter();

          //noinspection ResultOfMethodCallIgnored
          opt.map(x -> {
            con.inc();
            assertNull(x, opt.getClass().getSimpleName());
            return null;
          });

          assertEquals(1, con.get());
        }
      }

      @Test
      @DisplayName(
        "returns a new non-empty option wrapping null when the mapping " +
          "function returns null")
      void test1() {
        var opt1 = fullOption(123);
        var opt2 = opt1.map(x -> null);

        assertFalse(opt2.isEmpty());
        assertTrue(opt2.isNull());
      }
    }
  }

  @Nested
  @DisplayName("#map(Function<T, R>, Supplier<R>)")
  public class Map2 extends OptionContractTest.Map2 {

    @Nested
    @DisplayName(NullText)
    public class Null {

      @Test
      @DisplayName("returns an option wrapping null if the given function returns null.")
      public void t1() {
        var o = nullOption();
        assertTrue(o.map(nullRetMap(), tSup()).isNull(), o.getClass().getSimpleName());
      }

      @Test
      @DisplayName("returns an option wrapping the returned value from the given function.")
      public void t2() {
        for (var input : inputList()) {
          assertSame(input, nullOption().map(fn1(input), tSup()).unwrap());
        }
      }
    }
  }

  @Nested
  @DisplayName("#stream()")
  public class Stream1 extends OptionContractTest.Stream1 {

    @Nested
    @DisplayName(NullText)
    public class Null {

      @Test
      @DisplayName("returns a stream containing a single null value")
      public void t1() {
        var c = new S.Counter();
        var o = nullOption();

        o.stream().forEach(v -> {
          assertNull(v, o.getClass().getSimpleName());
          c.inc();
        });

        assertEquals(1, c.get(), o.getClass().getSimpleName());
      }
    }
  }

  @Nested
  @DisplayName("#valueEquals(Object)")
  public class ValueEquals1 extends ValueEquals {

    @Nested
    @DisplayName(NullText)
    public class Null {

      @Test
      @DisplayName("returns true for null inputs.")
      public void t1() {
        assertTrue(nullOption().valueEquals(null), nullOption().getClass().getSimpleName());
      }
    }
  }

  @Nested
  @DisplayName("#ifNull(Runnable)")
  public class IfNull1 {

    @Nested
    @DisplayName(NullText)
    public class Null {

      @Test
      @DisplayName("calls the given runnable 1 time.")
      public void t1() {
        var c = new S.Counter();

        nullOption().ifNull(c::inc);

        assertEquals(1, c.get(), () -> nullOption().getClass().getSimpleName());
      }
    }

    @Nested
    @DisplayName(NotNullText)
    public class NotNull {

      @Test
      @DisplayName("does not call the given runnable.")
      public void t1() {
        var o = fullOption("Lucifer");
        assertDoesNotThrow(
          () -> o.ifNull(() -> { throw new RuntimeException(); }),
          () -> o.getClass().getSimpleName());
      }
    }
  }
}