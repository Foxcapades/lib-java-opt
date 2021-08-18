package io.foxcapades.lib.opt;

import io.foxcapades.lib.opt.F.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("Option<T>")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class OptionContractTest {
  protected static final String FNEO     = "called on a non-empty option";
  protected static final String FEO      = "called on an empty option";
  protected static final String FBO      = "called on either an empty or non-empty option";
  protected static final String RetFalse = "returns false.";
  protected static final String RetTrue  = "returns true.";
  protected static final String RetWrap  = "returns the value wrapped by the option.";
  protected static final String NullSup  = "throws a NullPointerException if the given supplier is null";
  protected static final String NullFun  = "throws a NullPointerException if the given function is null";

  protected static final FN0<?> TSup = () -> {throw new RuntimeException();};

  protected final OptionContractTest self = this;

  protected abstract <T> List<? extends Option<T>> fullOptions(T value);

  protected abstract <T> Option<T> emptyOption();

  protected abstract <T> Option<T> fullOption(T value);

  protected abstract <T> Stream<? extends Option<T>> allOptionStream(T value);

  protected abstract <T> List<? extends Option<T>> allOptions(T value);

  @Deprecated
  protected Stream<? extends Option<Object>> fullOptions() {
    return inputStream().map(this::fullOption);
  }

  protected <I> List<? extends Option<I>> emptyOptions() {
    return List.of(emptyOption());
  }

  protected Stream<Object> inputStream() {
    return inputList().stream();
  }

  protected List<Object> inputList() {
    return Arrays.asList(
      666,
      new Object(),
      420,
      "Hello",
      69
    );
  }

  public <I, R> N1<I, R> nullRetMap() {return x -> null;}

  public <R> FN0<R> nullRetSup()      {return () -> null;}

  public <I, R> N1<I, R> nullMap()    {return null;}

  public <R> FN0<R> nullSup()         {return null;}

  public <R> V1<R> nullCon()          {return null;}

  public V0 nullRun()                 {return null;}

  public <I, R> N1<I, R> tMap()       {return i -> {throw new RuntimeException();};}

  public <R> FN0<R> tSup()            {return () -> {throw new RuntimeException();};}

  public <I> V1<I> tCon()             {return i -> {throw new RuntimeException();};}

  public V0 tRun()                    {return () -> {throw new RuntimeException();};}

  public V0 tRun(RuntimeException e)  {return () -> {throw e;};}

  public <R> FN0<R> fn0(R val)        {return () -> val;}

  public <I, R> N1<I, R> fn1(R val)   {return i -> val;}

  public V0 v0()                      {return () -> {};}

  public <I> V1<I> v1()               {return i -> {};}

  public <I> P<I> tP()                {return i -> {throw new RuntimeException();};}

  //
  //
  // Contract Tests
  //
  //

  @Nested
  @DisplayName("#isPresent()")
  protected class IsPresent {

    @Nested
    @DisplayName(FNEO)
    protected class Full {

      @Test
      @DisplayName("returns true")
      void t1() {
        for (var opt : fullOptions("taco"))
          assertTrue(opt.isPresent(), opt.getClass().getSimpleName());
      }
    }

    @Nested
    @DisplayName(FEO)
    protected class t2 {

      @Test
      @DisplayName(RetFalse)
      void test2() {
        for (var opt : emptyOptions())
          assertFalse(opt.isPresent(), opt.getClass().getSimpleName());
      }
    }
  }

  @Nested
  @DisplayName("#isEmpty()")
  protected class IsEmpty {

    @Nested
    @DisplayName(FNEO)
    protected class Full {

      @Test
      @DisplayName("returns false")
      protected void t1() {
        for (var opt : fullOptions("Nona"))
          assertFalse(opt.isEmpty(), opt.getClass().getSimpleName());
      }
    }

    @Nested
    @DisplayName(FEO)
    protected class Empty {

      @Test
      @DisplayName(RetTrue)
      void test1() {
        for (var opt : emptyOptions())
          assertTrue(opt.isEmpty(), opt.getClass().getSimpleName());
      }
    }
  }

  @Nested
  @DisplayName("#unwrap()")
  protected class Unwrap {

    @Nested
    @DisplayName(FNEO)
    protected class Full {

      @Test
      @DisplayName("returns the wrapped value")
      protected void t1() {
        var input = "puppies";
        for (var opt : fullOptions(input))
          assertSame(input, opt.unwrap(), opt.getClass().getSimpleName());
      }
    }

    @Nested
    @DisplayName(FEO)
    protected class t2 {

      @Test
      @DisplayName("throws an UnwrapException.")
      void test1() {

        for (var opt : emptyOptions())
          //noinspection ResultOfMethodCallIgnored
          assertThrows(UnwrapException.class, opt::unwrap, opt.getClass().getSimpleName());
      }
    }
  }

  @Nested
  @DisplayName("#or(T)")
  protected class Or {

    @TestFactory
    @DisplayName(FNEO)
    protected Stream<DynamicNode> nonEmptyOptions() {
      return inputStream()
        .map(D.Same(RetWrap, self::fullOption, Option::or, null));
    }

    @Nested
    @DisplayName(FEO)
    protected class t2 {
      @Test
      @DisplayName("returns the input value.")
      void test1() {
        A.EQ(234, emptyOption().or(234));
      }
    }
  }

  @Nested
  @DisplayName("#orGet(Supplier<T>)")
  protected class OrGet {

    @TestFactory
    @DisplayName(FNEO)
    protected Stream<DynamicNode> nonEmptyOptions() {
      return inputStream()
        .flatMap(D.Group(
          D.Same(RetWrap, self::fullOption, Option::orGet, fn0(3)),
          D.DNT("does not call the given supplier.", self::fullOption, Option::orGet, tSup())));
    }

    @TestFactory
    @DisplayName(FEO)
    protected Stream<DynamicNode> forEmptyOptions() {
      return Stream.of(
        dynamicTest(
          "returns the value provided by the given supplier",
          () -> inputStream().forEach(v -> A.Same(v, emptyOption().orGet(fn0(v))))));
    }

    @TestFactory
    @DisplayName(FBO)
    protected Stream<DynamicNode> forBothOptions() {
      return allOptionStream(3)
        .map(D.ThrowsNPE(NullSup, Option::orGet, self.<Integer>nullSup()));
    }
  }

  @Nested
  @DisplayName("#orThrow(E)")
  protected class OrThrow1 {

    @Nested
    @DisplayName(FNEO)
    public class Full {

      @Test
      @DisplayName("does not throw the given exception.")
      public void t1() {
        for (var opt : fullOptions("flesh")) {
          assertDoesNotThrow(
            () -> opt.orThrow(new RuntimeException()),
            opt.getClass().getSimpleName()
          );
        }
      }

      @Test
      @DisplayName("returns the wrapped value")
      public void t2() {
        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            assertSame(input, opt.orThrow(new RuntimeException()), opt.getClass().getSimpleName());
          }
        }
      }
    }

    @TestFactory
    @DisplayName(FEO)
    protected Stream<DynamicNode> empty() {
      return Stream.of(emptyOption())
        .map(D.Throws("throws the given exception.", Option::orThrow, new IllegalArgumentException()));
    }

    @Nested
    @DisplayName(FBO)
    public class Both {

      @Test
      @DisplayName("throws a NullPointerException if given a null exception value.")
      public void v1() {
        for (var opt : allOptions("ghost")) {
          //noinspection ConstantConditions,ResultOfMethodCallIgnored
          assertThrows(
            NullPointerException.class,
            () -> opt.orThrow(null),
            opt.getClass().getSimpleName()
          );
        }
      }
    }
  }

  @Nested
  @DisplayName("#orElseThrow(Supplier<E>)")
  protected class OrThrow2 {

    @TestFactory
    @DisplayName(FNEO)
    protected Stream<DynamicNode> forNonEmptyOptions() {
      N2<Option<Object>, FN0<RuntimeException>, Object> fn = Option::orElseThrow;

      return fullOptions().flatMap(D.Group(
        D.DNT("does not throw the given exception.", fn, RuntimeException::new),
        D.DNT("does not call the given supplier.", fn, tSup())
      ));
    }

    @Nested
    @DisplayName(FEO)
    protected class t2 {

      @Test
      @DisplayName("throws the given exception.")
      void test4() {
        //noinspection ResultOfMethodCallIgnored
        A.Throws(IllegalStateException.class, () -> emptyOption().orElseThrow(IllegalStateException::new));
      }

      @Test
      @DisplayName("throws a NullPointerException when the given Supplier returns null.")
      void test6() {
        //noinspection LambdaBodyCanBeCodeBlock,ResultOfMethodCallIgnored
        A.ThrowsNPE(() -> emptyOption().orElseThrow(nullRetSup()));
      }
    }

    @TestFactory
    @DisplayName(FBO)
    protected Stream<DynamicNode> forBoth() {
      return allOptionStream(420).map(D.ThrowsNPE(NullSup, Option::orElseThrow, self.<RuntimeException>nullSup()));
    }
  }

  @Nested
  @DisplayName("#map(Function<T, R>)")
  protected class Map1 {

    @Nested
    @DisplayName(FNEO)
    protected class Full {

      @Test
      @DisplayName("calls the given Function passing in the wrapped value.")
      void t1() {
        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            var c = new S.Counter();

            //noinspection ResultOfMethodCallIgnored
            opt.map(i -> {
              assertSame(input, i, opt.getClass().getSimpleName());
              return c.inc(i);
            });

            assertEquals(1, c.get(), opt.getClass().getSimpleName());
          }
        }
      }

      @Test
      @DisplayName("returns a new, non-empty option wrapping the value retrieved from " +
        "the input function.")
      void t2() {
        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            for (var i2 : inputList()) {
              assertSame(i2, opt.map(fn1(i2)).unwrap(), opt.getClass().getSimpleName());
            }
          }
        }
      }
    }

    @Nested
    @DisplayName(FEO)
    protected class Empty {

      @Test
      @DisplayName("does not call the given function")
      void t1() {
        for (var opt : emptyOptions()) {
          assertDoesNotThrow(() -> opt.map(tMap()), opt.getClass().getSimpleName());
        }
      }

      @Test
      @DisplayName("returns an empty option")
      void t2() {
        for (var opt : emptyOptions())
          assertTrue(opt.map(fn1(3)).isEmpty(), opt.getClass().getSimpleName());
      }
    }

    @Nested
    @DisplayName(FBO)
    protected class Both {

      @Test
      @DisplayName("throws a NullPointerException if the given input is null")
      void t1() {
        for (var input : inputList()) {
          for (var opt : allOptions(input)) {
            //noinspection ResultOfMethodCallIgnored
            assertThrows(
              NullPointerException.class,
              () -> opt.map(nullMap()),
              opt.getClass().getSimpleName()
            );
          }
        }
      }
    }

    @TestFactory
    @DisplayName(FBO)
    protected Stream<DynamicNode> forBothOptions() {
      return allOptionStream("grapes")
        .map(D.ThrowsNPE(
          "throws a NullPointerException if the given input is null.",
          Option::map,
          nullMap()
        ));
    }
  }

  @Nested
  @DisplayName("#map(Function<T, R>, Supplier<R>)")
  protected class Map2 {

    @Nested
    @DisplayName(FNEO)
    protected class Full {

      @Test
      @DisplayName("does not call the given supplier.")
      void t1() {
        for (var opt : fullOptions(33)) {
          assertDoesNotThrow(() -> opt.map(x -> x, tSup()), opt.getClass().getSimpleName());
        }
      }

      @Test
      @DisplayName("calls the given mapping function with the wrapped value.")
      void t2() {
        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            var c = new S.Counter();

            //noinspection ResultOfMethodCallIgnored
            opt.map(x -> {
              assertSame(input, x, opt.getClass().getSimpleName());
              return c.inc(x);
            }, tSup());

            assertEquals(1, c.get(), opt.getClass().getSimpleName());
          }
        }
      }
    }

    @TestFactory
    @DisplayName(FEO)
    protected Stream<DynamicNode> empty() {
      FN3<Option<Object>, N1<Object, Object>, FN0<Object>, Object> fn = Option::map;

      return Stream.of(emptyOption())
        .flatMap(D.Group(
          D.DNT("does not call the given mapping function.", fn, tMap(), Object::new),
          D.Test("calls the given supplier.", o -> {
            final var c = new S.Counter();

            //noinspection ResultOfMethodCallIgnored
            o.map(tMap(), () -> {
              c.inc();
              return null;
            });

            A.EQ(1, c.get());
          }),
          D.Test("returns an option wrapping the value supplied by the given supplier.", o -> {
            final var r = "potato";

            A.Same(r, o.map(tMap(), fn0(r)).unwrap());
          })
        ));
    }

    @TestFactory
    @DisplayName(FBO)
    protected Stream<DynamicNode> forBothEmptyAndNonEmptyOptions() {
      return allOptionStream("hiya")
        .flatMap(D.Group(
          D.ThrowsNPE("throws a NullPointerException if arg 1 is null.", Option::map, nullMap(), fn0(3)),
          D.ThrowsNPE("throws a NullPointerException if arg 2 is null.", Option::map, fn1(3), nullSup())));
    }
  }

  @Nested
  @DisplayName("#flatMap(Function<T, Option<R>)")
  protected class FlatMap1 {
    static final N2<Option<Object>, N1<Object, Option<Object>>, Option<? super Object>> fn = Option::flatMap;

    @Nested
    @DisplayName(FNEO)
    protected class Full {

      @Test
      @DisplayName("passes the wrapped value to the input function")
      void t1() {

        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            var c = new S.Counter();

            //noinspection ResultOfMethodCallIgnored
            opt.flatMap(v -> {
              assertSame(input, v, opt.getClass().getSimpleName());
              return c.inc(opt);
            });

            assertEquals(1, c.get(), opt.getClass().getSimpleName());
          }
        }
      }

      @Test
      @DisplayName("returns the value provided by the given mapping function")
      void t2() {
        var out = fullOption("hello");

        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            assertSame(out, opt.flatMap(__ -> out), opt.getClass().getSimpleName());
          }
        }
      }

      @Test
      @DisplayName("throws a NullPointerException if the given mapping function returns null")
      void t3() {
        for (var opt : fullOptions(666)) {
          //noinspection ResultOfMethodCallIgnored
          assertThrows(NullPointerException.class, () -> opt.flatMap(f -> null), opt.getClass().getSimpleName());
        }
      }
    }

    @TestFactory
    @DisplayName(FEO)
    protected Stream<DynamicNode> forEmpty() {
      N1<Object, Option<Object>> errFMap = x -> {throw new RuntimeException();};

      return Stream.of(emptyOption())
        .map(D.DNT("does not call the given mapping function", fn, errFMap));
    }

    @TestFactory
    @DisplayName(FBO)
    protected Stream<DynamicNode> forBoth() {
      return allOptionStream(666).map(D.ThrowsNPE(NullFun, Option::flatMap, self.<Integer, Option<Object>>nullMap()));
    }
  }

  @Nested
  @DisplayName("#flatMap(Function<T, Option<R>, Supplier<Option<R>>)")
  protected class FlatMap2 {

    @Nested
    @DisplayName(FNEO)
    protected class Full {

      @Test
      @DisplayName("throws a NullPointerException if the given mapping function returns null.")
      void t1() {
        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            //noinspection ResultOfMethodCallIgnored
            assertThrows(
              NullPointerException.class,
              () -> opt.flatMap(nullRetMap(), fn0(fullOption(2))),
              opt.getClass().getSimpleName()
            );
          }
        }
      }

      @Test
      @DisplayName("does not call the given supplier.")
      void t2() {
        for (var opt : fullOptions(69))
          assertDoesNotThrow(() -> opt.flatMap(i -> opt, tSup()), opt.getClass().getSimpleName());
      }
    }

    @Nested
    @DisplayName(FEO)
    protected class Empty {

      @Test
      @DisplayName("throws a NullPointerException if the given supplier function returns null")
      void t1() {
        for (var opt : emptyOptions()) {
          //noinspection ResultOfMethodCallIgnored
          assertThrows(
            NullPointerException.class,
            () -> opt.flatMap(fn1(opt), nullRetSup()),
            opt.getClass().getSimpleName()
          );
        }
      }

      @Test
      @DisplayName("does not call the given mapping function")
      void t2() {
        for (var opt : emptyOptions()) {
          assertDoesNotThrow(
            () -> opt.flatMap(tMap(), fn0(opt)),
            opt.getClass().getSimpleName()
          );
        }
      }
    }

    @TestFactory
    @DisplayName(FBO)
    protected Stream<DynamicNode> both() {
      FN3<Option<String>, N1<String, Option<Object>>, FN0<Option<Object>>, Option<Object>> fn = Option::flatMap;
      return allOptionStream("yellow").flatMap(D.Group(
        D.ThrowsNPE("throws a NullPointerException if arg1 is null", fn, nullMap(), self::emptyOption),
        D.ThrowsNPE("throws a NullPointerException if arg1 is null", fn, self::fullOption, nullSup())
      ));
    }
  }

  @Nested
  @DisplayName("#stream()")
  protected class Stream1 {

    @Nested
    @DisplayName(FNEO)
    protected class Full {

      @Test
      @DisplayName("returns a stream containing the wrapped value")
      void t1() {
        for (var opt : fullOptions(55.55))
          //noinspection OptionalGetWithoutIsPresent
          assertSame(opt.unwrap(), opt.stream().findFirst().get());
      }

      @Test
      @DisplayName("returns a stream containing only 1 value")
      void t2() {
        for (var opt : fullOptions("harmonious"))
          assertEquals(1, opt.stream().count());
      }
    }

    @Nested
    @DisplayName(FEO)
    protected class Empty {

      @Test
      @DisplayName("returns an empty stream")
      void t1() {
        for (var opt : emptyOptions())
          assertEquals(0, opt.stream().count());
      }
    }
  }

  @Nested
  @DisplayName("#ifPresent(Consumer<T>)")
  protected class IfPresent1 {

    protected <I> N2<Option<I>, V1<I>, Option<I>> ref() {
      return Option::ifPresent;
    }

    @TestFactory
    @DisplayName(FNEO)
    protected Stream<DynamicNode> full() {
      return fullOptions().map(
        D.Test("calls the given consumer with the wrapped value", o -> {
          var c = new S.Counter();

          o.ifPresent(i -> {
            c.inc();
            A.Same(o.unwrap(), i);
          });

          A.EQ(1, c.get());
        })
      );
    }

    @TestFactory
    @DisplayName(FEO)
    protected Stream<DynamicNode> empty() {
      return Stream.of(emptyOption())
        .map(D.DNT("does not call the given consumer", ref(), tCon()));
    }

    @TestFactory
    @DisplayName(FBO)
    protected Stream<DynamicNode> both() {
      return allOptionStream(321)
        .flatMap(D.Group(
          D.ThrowsNPE("throws a NullPointerException if the given consumer is null", ref()),
          D.ThisReturn(ref(), v1())
        ));
    }
  }

  @Nested
  @DisplayName("#ifEmpty(Runnable)")
  protected class IfEmpty1 {

    protected static class DerpX extends RuntimeException {}

    protected static <I> N2<Option<I>, V0, Option<I>> ref() {
      return Option::ifEmpty;
    }

    @TestFactory
    @DisplayName(FNEO)
    protected Stream<DynamicNode> full() {
      return fullOptions().map(D.DNT("does not call the given runnable", ref(), tRun()));
    }

    @TestFactory
    @DisplayName(FEO)
    protected Stream<DynamicNode> empty() {
      return emptyOptions()
        .stream()
        .map(D.Throws("calls the given runnable", DerpX.class, ref(), tRun(new DerpX())));
    }

    @TestFactory
    @DisplayName(FBO)
    protected Stream<DynamicNode> both() {
      return allOptionStream(666).flatMap(D.Group(
        D.ThrowsNPE("throws a NullPointerException if the given runnable is null", ref()),
        D.ThisReturn(ref(), v0())
      ));
    }
  }

  @Nested
  @DisplayName("#with(Consumer<T>, Runnable)")
  protected class With1 {

    protected static class DerpY extends RuntimeException {}

    protected <I> FN3<Option<I>, V1<I>, V0, Option<I>> ref() {
      return Option::with;
    }

    @TestFactory
    @DisplayName(FNEO)
    protected Stream<DynamicNode> full() {
      return fullOptions().flatMap(D.Group(
        D.Test("calls the given consumer with the wrapped value", o -> {
          var c = new S.Counter();

          o.with(i -> {
            c.inc();
            A.Same(o.unwrap(), i);
          }, v0());

          A.EQ(1, c.get());
        }),
        D.DNT("does not call the given runnable", ref(), v1(), tRun())
      ));
    }

    @TestFactory
    @DisplayName(FEO)
    protected Stream<DynamicNode> empty() {
      return emptyOptions().stream().flatMap(D.Group(
        D.DNT("does not call the given consumer", ref(), tCon(), v0()),
        D.Throws("calls the given runnable", DerpY.class, ref(), tCon(), tRun(new DerpY()))
      ));
    }

    @Nested
    @DisplayName(FBO)
    public class Both {

      @Test
      @DisplayName("throws a NullPointerException if arg 1 is null")
      public void t1() {
        for (var opt : allOptions("obelisk")) {
          assertThrows(NullPointerException.class, () -> opt.with(nullCon(), v0()));
        }
      }

      @Test
      @DisplayName("throws a NullPointerException if arg 2 is null")
      public void t2() {
        for (var opt : allOptions("leviathan")) {
          assertThrows(NullPointerException.class, () -> opt.with(v1(), nullRun()));
        }
      }

      @Test
      @DisplayName("returns the same option instance")
      public void t3() {
        for (var opt : allOptions("לויתן")) {
          assertSame(opt, opt.with(v1(), v0()));
        }
      }
    }
  }

  @Nested
  @DisplayName("#filter(Predicate<T>)")
  protected class Filter1 {

    protected static <I> N2<Option<I>, Predicate<I>, Option<I>> ref() {
      return Option::filter;
    }

    @Nested
    @DisplayName(FNEO)
    protected class Full {

      @Test
      @DisplayName("returns an empty option when the predicate returns false.")
      void t1() {
        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            assertTrue(opt.filter(i -> false).isEmpty(), opt.getClass().getSimpleName());
          }
        }
      }

      @Test
      @DisplayName("returns an option wrapping the same value when the predicate returns true.")
      void t2() {
        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            assertSame(input, opt.filter(i -> true).unwrap(), opt.getClass().getSimpleName());
          }
        }
      }
    }

    @TestFactory
    @DisplayName(FEO)
    protected Stream<DynamicNode> empty() {
      return emptyOptions()
        .stream()
        .flatMap(D.Group(
          D.DNT("does not call the given predicate", ref(), tP()),
          D.Test("returns an empty option", i -> A.True(i.filter(tP()).isEmpty()))
        ));
    }

    @TestFactory
    @DisplayName(FBO)
    protected Stream<DynamicNode> both() {
      return allOptionStream(69)
        .map(D.ThrowsNPE("throws a NullPointerException if the given predicate is null", ref()));
    }
  }

  @Nested
  @DisplayName("#valueEquals(Object)")
  protected class ValueEquals {

    @Nested
    @DisplayName(FNEO)
    protected class Full {

      @Test
      @DisplayName("returns true if the given value equals the wrapped value.")
      void t1() {
        for (var opt : fullOptions(33))
          assertTrue(opt.valueEquals(33), opt.getClass().getSimpleName());
      }

      @Test
      @DisplayName("returns false if the given value does not equal the wrapped value.")
      void t2() {
        for (var opt : fullOptions(36))
          assertFalse(opt.valueEquals(43), opt.getClass().getSimpleName());
      }
    }

    @Nested
    @DisplayName(FEO)
    protected class Empty {

      @Test
      @DisplayName("returns false.")
      void t1() {
        for (var opt : emptyOptions())
          assertFalse(opt.valueEquals(null), opt.getClass().getSimpleName());
      }
    }
  }
}
