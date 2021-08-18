package io.foxcapades.lib.opt;

import java.util.function.*;

public interface F {

  interface P<I> extends Predicate<I> {}

  interface FN0<R> extends Supplier<R> {}
  interface N1<A, R> extends Function<A, R> {}
  interface N2<A, B, R> extends BiFunction<A, B, R> {}
  interface FN3<A, B, C, R> {
    R apply(A a, B b, C c);
  }
  interface V0 extends Runnable {}
  interface V1<A> extends Consumer<A> {}

  interface TFN2<A, B, R, E extends Throwable> { R apply(A a, B b) throws E; }

}
