package io.foxcapades.lib.opt;

import io.foxcapades.lib.opt.F.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Option<T>")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class OptionContractTest {
  protected static final String FNEO     = "called on a non-empty option";
  protected static final String FEO      = "called on an empty option";
  protected static final String BothText = "called on either an empty or non-empty option";
  protected static final String RetFalse = "returns false.";
  protected static final String RetTrue  = "returns true.";
  protected static final String RetWrap  = "returns the value wrapped by the option.";
  protected static final String NullSup  = "throws a NullPointerException if the given supplier is null";
  protected static final String NullFun  = "throws a NullPointerException if the given function is null";

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

    @Nested
    @DisplayName(FNEO)
    public class Full {

      @Test
      @DisplayName(RetWrap)
      public void t1() {
        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            assertSame(input, opt.or("Sut"), opt.getClass().getSimpleName());
          }
        }
      }
    }

    @Nested
    @DisplayName(FEO)
    protected class t2 {

      @Test
      @DisplayName("returns the input value.")
      void test1() {
        assertEquals(234, emptyOption().or(234));
      }
    }
  }

  @Nested
  @DisplayName("#orGet(Supplier<T>)")
  protected class OrGet {

    @Nested
    @DisplayName(FNEO)
    public class Full {

      @Test
      @DisplayName(RetWrap)
      public void t1() {
        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            assertSame(input, opt.orGet(() -> "Seir"));
          }
        }
      }

      @Test
      @DisplayName("does not call the given supplier.")
      public void t2() {
        for (var opt : fullOptions("Orobas")) {
          assertDoesNotThrow(() -> opt.orGet(() -> { throw new RuntimeException(); }), opt.getClass().getSimpleName());
        }
      }
    }

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("returns the value provided by the given supplier.")
      public void t1() {
        for (var input : inputList()) {
          for (var opt : emptyOptions()) {
            assertSame(input, opt.orGet(() -> input), opt.getClass().getSimpleName());
          }
        }
      }
    }

    @Nested
    @DisplayName(BothText)
    public class Both {

      @Test
      @DisplayName(NullSup)
      public void t1() {
        for (var opt : allOptions("Cerbere")) {
          //noinspection ConstantConditions,ResultOfMethodCallIgnored
          assertThrows(
            NullPointerException.class,
            () -> opt.orGet(null),
            opt.getClass().getSimpleName()
          );
        }
      }
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

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("throws the given exception.")
      public void t1() {
        for (var opt : emptyOptions()) {
          //noinspection ResultOfMethodCallIgnored
          assertThrows(
            IllegalStateException.class,
            () -> opt.orThrow(new IllegalStateException()),
            opt.getClass().getSimpleName()
          );
        }
      }
    }

    @Nested
    @DisplayName(BothText)
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
  public class OrThrow2 {

    @Nested
    @DisplayName(FNEO)
    public class Full {

      @Test
      @DisplayName("does not throw the given exception.")
      public void t1() {
        for (var opt : fullOptions("Foraii")) {
          assertDoesNotThrow(
            () -> opt.orElseThrow(RuntimeException::new),
            opt.getClass().getSimpleName()
          );
        }
      }

      @Test
      @DisplayName("does not call the given supplier.")
      public void t2() {
        for (var opt : fullOptions("Mammon")) {
          assertDoesNotThrow(
            () -> opt.orElseThrow(() -> {throw new RuntimeException();}),
            opt.getClass().getSimpleName()
          );
        }
      }

      @Test
      @DisplayName("returns the wrapped value.")
      public void t3() {
        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            assertSame(input, opt.orElseThrow(RuntimeException::new), opt.getClass().getSimpleName());
          }
        }
      }
    }

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("throws the given exception.")
      public void t1() {
        for (var opt : emptyOptions()) {
          //noinspection ResultOfMethodCallIgnored
          assertThrows(
            IllegalStateException.class,
            () -> opt.orElseThrow(IllegalStateException::new),
            opt.getClass().getSimpleName()
          );
        }
      }

      @Test
      @DisplayName("throws a NullPointerException when the given Supplier returns null.")
      public void t2() {
        for (var opt : emptyOptions()) {
          //noinspection ResultOfMethodCallIgnored
          assertThrows(
            NullPointerException.class,
            () -> opt.orElseThrow(nullRetSup()),
            opt.getClass().getSimpleName()
          );
        }
      }
    }

    @Nested
    @DisplayName(BothText)
    public class Both {

      @Test
      @DisplayName(NullSup)
      public void t1() {
        for (var opt : allOptions("Lilith")) {
          //noinspection ResultOfMethodCallIgnored
          assertThrows(
            NullPointerException.class,
            () -> opt.orElseThrow(nullSup()),
            opt.getClass().getSimpleName()
          );
        }
      }
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
    @DisplayName(BothText)
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

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("does not call the given mapping function.")
      public void t1() {
        for (var opt : emptyOptions()) {
          assertDoesNotThrow(
            () -> opt.map(tMap(), Object::new),
            opt.getClass().getSimpleName()
          );
        }
      }

      @Test
      @DisplayName("calls the given supplier 1 time.")
      public void t2() {
        for (var opt : emptyOptions()) {
          var c = new S.Counter();

          //noinspection ResultOfMethodCallIgnored
          opt.map(tMap(), () -> c.inc(new Object()));

          assertEquals(1, c.get(), opt.getClass().getSimpleName());
        }
      }

      @Test
      @DisplayName("returns an option wrapping the value supplied by the given supplier.")
      public void t3() {
        for (var input : inputList()) {
          for (var opt : emptyOptions()) {
            assertSame(input, opt.map(tMap(), () -> input).unwrap(), opt.getClass().getSimpleName());
          }
        }
      }
    }

    @Nested
    @DisplayName(BothText)
    public class Both {

      @Test
      @DisplayName("throws a NullPointerException if arg 1 is null.")
      public void t1() {
        for (var opt : allOptions("Jinn")) {
          //noinspection ResultOfMethodCallIgnored
          assertThrows(
            NullPointerException.class,
            () -> opt.map(nullMap(), () -> "Ipes"),
            opt.getClass().getSimpleName()
          );
        }
      }

      @Test
      @DisplayName("throws a NullPointerException if arg 2 is null.")
      public void t2() {
        for (var opt : allOptions("Malthus")) {
          //noinspection ResultOfMethodCallIgnored
          assertThrows(
            NullPointerException.class,
            () -> opt.map(fn1("Haagenti"), nullSup()),
            opt.getClass().getSimpleName()
          );
        }
      }
    }
  }

  @Nested
  @DisplayName("#flatMap(Function<T, Option<R>)")
  protected class FlatMap1 {

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

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("does not call the given mapping function.")
      public void t1() {
        for (var opt : emptyOptions()) {
          assertDoesNotThrow(
            () -> opt.flatMap(tMap()),
            opt.getClass().getSimpleName()
          );
        }
      }
    }

    @Nested
    @DisplayName(BothText)
    public class Both {

      @Test
      @DisplayName(NullFun)
      public void t1() {
        for (var opt : allOptions("Gader'el")) {
          //noinspection ResultOfMethodCallIgnored,ConstantConditions
          assertThrows(
            NullPointerException.class,
            () -> opt.flatMap(null),
            opt.getClass().getSimpleName()
          );
        }
      }
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

    @Nested
    @DisplayName(BothText)
    public class Both {

      @Test
      @DisplayName("throws a NullPointerException if arg 1 is null.")
      public void t1() {
        for (var opt : allOptions("Belphegor")) {
          //noinspection ConstantConditions,ResultOfMethodCallIgnored
          assertThrows(
            NullPointerException.class,
            () -> opt.flatMap(null, OptionContractTest.this::emptyOption),
            opt.getClass().getSimpleName()
          );
        }
      }

      @Test
      @DisplayName("throws a NullPointerException if arg 2 is null.")
      public void t2() {
        for (var opt : allOptions("Andras")) {
          //noinspection ResultOfMethodCallIgnored,ConstantConditions
          assertThrows(
            NullPointerException.class,
            () -> opt.flatMap(OptionContractTest.this::fullOption, null),
            opt.getClass().getSimpleName()
          );
        }
      }
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

    @Nested
    @DisplayName(FNEO)
    public class Full {

      @Test
      @DisplayName("calls the given consumer with the wrapped value.")
      public void t1() {
        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            var c = new S.Counter();

            opt.ifPresent(x -> {
              c.inc();
              assertSame(input, x, opt.getClass().getSimpleName());
            });

            assertEquals(1, c.get(), opt.getClass().getSimpleName());
          }
        }
      }
    }

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("does not call the given consumer.")
      public void t1() {
        for (var opt : emptyOptions()) {
          assertDoesNotThrow(
            () -> opt.ifPresent(tCon()),
            opt.getClass().getSimpleName()
          );
        }
      }
    }

    @Nested
    @DisplayName(BothText)
    public class Both {

      @Test
      @DisplayName("throws a NullPointerException if the given consumer is null.")
      public void t1() {
        for (var opt : allOptions("Mephistopheles")) {
          //noinspection ConstantConditions
          assertThrows(
            NullPointerException.class,
            () -> opt.ifPresent(null),
            opt.getClass().getSimpleName()
          );
        }
      }

      @Test
      @DisplayName("returns the current option instance.")
      public void t2() {
        for (var opt : allOptions("Penemue")) {
          assertSame(opt, opt.ifPresent(x -> {}), opt.getClass().getSimpleName());
        }
      }
    }
  }

  @Nested
  @DisplayName("#ifEmpty(Runnable)")
  protected class IfEmpty1 {

    @Nested
    @DisplayName(FNEO)
    public class Full {

      @Test
      @DisplayName("does not call the given runnable.")
      public void t1() {
        for (var opt : fullOptions("Shaitan")) {
          assertDoesNotThrow(
            () -> opt.ifEmpty(tRun()),
            opt.getClass().getSimpleName()
          );
        }
      }
    }

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("calls the given runnable 1 time.")
      public void t1() {
        for (var opt : emptyOptions()) {
          var c = new S.Counter();

          opt.ifEmpty(c::inc);

          assertEquals(1, c.get(), opt.getClass().getSimpleName());
        }
      }
    }

    @Nested
    @DisplayName(BothText)
    public class Both {

      @Test
      @DisplayName("throws a NullPointerException if the given runnable is null.")
      public void t1() {
        for (var opt : allOptions("Satan")) {
          //noinspection ConstantConditions
          assertThrows(
            NullPointerException.class,
            () -> opt.ifEmpty(null),
            opt.getClass().getSimpleName()
          );
        }
      }

      @Test
      @DisplayName("returns the current option instance.")
      public void t2() {
        for (var opt : allOptions("Vepar")) {
          assertSame(opt, opt.ifEmpty(() -> {}), opt.getClass().getSimpleName());
        }
      }
    }
  }

  @Nested
  @DisplayName("#with(Consumer<T>, Runnable)")
  protected class With1 {

    @Nested
    @DisplayName(FNEO)
    public class Full {

      @Test
      @DisplayName("calls the given consumer with the wrapped value.")
      public void t1() {
        for (var input : inputList()) {
          for (var opt : fullOptions(input)) {
            var c = new S.Counter();

            opt.with(x -> assertSame(input, c.inc(x), opt.getClass().getSimpleName()), () -> {});

            assertEquals(1, c.get(), opt.getClass().getSimpleName());
          }
        }
      }

      @Test
      @DisplayName("does not call the given runnable.")
      public void t2() {
        for (var opt : fullOptions("Ukobach")) {
          assertDoesNotThrow(
            () -> opt.with(v1(), tRun()),
            opt.getClass().getSimpleName()
          );
        }
      }
    }

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("does not call the given consumer.")
      public void t1() {
        for (var opt : emptyOptions()) {
          assertDoesNotThrow(
            () -> opt.with(tCon(), v0()),
            opt.getClass().getSimpleName()
          );
        }
      }

      @Test
      @DisplayName("calls the given runnable 1 time.")
      public void t2() {
        for (var opt : emptyOptions()) {
          var c = new S.Counter();

          opt.with(tCon(), c::inc);

          assertEquals(1, c.get(), opt.getClass().getSimpleName());
        }
      }
    }

    @Nested
    @DisplayName(BothText)
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

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("does not call the given predicate.")
      public void t1() {
        for (var opt : emptyOptions()) {
          assertDoesNotThrow(() -> opt.filter(tP()), opt.getClass().getSimpleName());
        }
      }

      @Test
      @DisplayName("returns an empty option.")
      public void t2() {
        for (var opt : emptyOptions()) {
          assertTrue(opt.filter(tP()).isEmpty(), opt.getClass().getSimpleName());
        }
      }
    }

    @Nested
    @DisplayName(BothText)
    public class Both {

      @Test
      @DisplayName("throws a NullPointerException if the given predicate is null.")
      public void t1() {
        for (var opt : allOptions("Xaphan")) {
          assertThrows(
            NullPointerException.class,
            () -> opt.filter(null),
            opt.getClass().getSimpleName()
          );
        }
      }
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

  @Nested
  @DisplayName("#orOption(Option<T>)")
  public class OrOption1 {

    @Nested
    @DisplayName(FNEO)
    public class Full {

      @Test
      @DisplayName("does not return the given option.")
      public void t1() {
        for (var opt : fullOptions("serpent")) {
          for (var other : fullOptions("blow your trumpets Gabriel")) {
            assertSame(opt, opt.orOption(other), opt.getClass().getSimpleName());
          }
        }
      }
    }

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("returns the given option.")
      public void t1() {
        for (var opt : OptionContractTest.this.<String>emptyOptions()) {
          for (var other : fullOptions("embrace the void")) {
            assertSame(other, opt.orOption(other), opt.getClass().getSimpleName());
          }
        }
      }
    }

    @Nested
    @DisplayName(BothText)
    public class Both {

      @Test
      @DisplayName("throws a NullPointerException if the input value is null.")
      public void t1() {
        for (var opt : allOptions("in the absence of light")) {
          //noinspection ResultOfMethodCallIgnored,ConstantConditions
          assertThrows(
            NullPointerException.class,
            () -> opt.orOption((Option<String>) null),
            opt.getClass().getSimpleName()
          );
        }
      }
    }
  }

  @Nested
  @DisplayName("#orOption(Supplier<Option<T>>)")
  public class OrOption2 {

    @Nested
    @DisplayName(FNEO)
    public class Full {

      @Test
      @DisplayName("does not return the given option.")
      public void t1() {
        for (var opt : fullOptions("Armaros")) {
          for (var other : fullOptions("blood worship")) {
            assertSame(opt, opt.orOption(() -> other), opt.getClass().getSimpleName());
          }
        }
      }
    }

    @Nested
    @DisplayName(FEO)
    public class Empty {

      @Test
      @DisplayName("returns the option supplied by the given function.")
      public void t1() {
        for (var opt : OptionContractTest.this.<String>emptyOptions()) {
          for (var other : fullOptions("Ifrit")) {
            assertSame(other, opt.orOption(() -> other), opt.getClass().getSimpleName());
          }
        }
      }

      @Test
      @DisplayName("throws a NullPointerException if the given supplier returns null.")
      public void t2() {
        for (var opt : emptyOptions()) {
          //noinspection ResultOfMethodCallIgnored
          assertThrows(NullPointerException.class, () -> opt.orOption(() -> null), opt.getClass().getSimpleName());
        }
      }
    }

    @Nested
    @DisplayName(BothText)
    public class Both {

      @Test
      @DisplayName("throws a NullPointerException if the input value is null.")
      public void t1() {
        for (var opt : allOptions("burn heaven down")) {
          //noinspection ResultOfMethodCallIgnored,ConstantConditions
          assertThrows(
            NullPointerException.class,
            () -> opt.orOption((Supplier<Option<String>>) null),
            opt.getClass().getSimpleName()
          );
        }
      }
    }
  }
}
