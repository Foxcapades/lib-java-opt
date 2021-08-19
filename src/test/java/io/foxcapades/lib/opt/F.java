package io.foxcapades.lib.opt;

import java.util.function.*;

public interface F {

  interface P<I> extends Predicate<I> {}

  interface FN0<R> extends Supplier<R> {}
  interface N1<A, R> extends Function<A, R> {}
  interface V0 extends Runnable {}
  interface V1<A> extends Consumer<A> {}

}
