package io.foxcapades.lib.opt;

import io.foxcapades.lib.opt.F.*;
import org.junit.jupiter.api.DynamicNode;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public interface D {

  //
  //
  // EXCEPTION THROWING
  //
  //

  static <I, A1, R> DNode<I> Throws(
    String msg,
    Class<? extends RuntimeException> x,
    N2<I, A1, R> fn,
    A1 arg
  ) {
    return v -> dynamicTest(msg, () -> A.Throws(x, () -> fn.apply(v, arg)));
  }

  static <I, A1, A2, R> DNode<I> Throws(
    String msg,
    Class<? extends RuntimeException> x,
    FN3<I, A1, A2, R> fn,
    A1 arg1,
    A2 arg2
  ) {
    return v -> dynamicTest(msg, () -> A.Throws(x, () -> fn.apply(v, arg1, arg2)));
  }

  static <I, R, E extends Throwable> DNode<I> Throws(String msg, TFN2<I, E, R, E> fn, E ex) {
    return v -> dynamicTest(msg, () -> assertThrows(ex.getClass(), () -> fn.apply(v, ex)));
  }

  static <I, A1, R> DNode<I> ThrowsNPE(String msg, N2<I, A1, R> fn) {
    return v -> {
      return dynamicTest(msg, () -> A.Throws(NullPointerException.class, () -> fn.apply(v, null)));
    };
  }

  static <I, A1, R> DNode<I> ThrowsNPE(String msg, N2<I, A1, R> fn, A1 a) {
    return v -> dynamicTest(msg, () -> A.Throws(NullPointerException.class, () -> fn.apply(v, a)));
  }


  static <I, A1, A2, R> DNode<I> ThrowsNPE(String msg, FN3<I, A1, A2, R> fn, A1 a, A2 b) {
    return v -> dynamicTest(msg, () -> A.Throws(NullPointerException.class, () -> fn.apply(v, a, b)));
  }

  //
  //
  // CUSTOM TESTS
  //
  //

  static <I> DNode<I> Test(String msg, V1<I> test) {
    return val -> dynamicTest(msg, () -> test.accept(val));
  }

  //
  //
  // Builder Returns
  //
  //

  static <I, A1> DNode<I> ThisReturn(N2<I, A1, I> fn, A1 arg) {
    return v -> dynamicTest("returns the current instance", () -> A.Same(v, fn.apply(v, arg)));
  }

  //
  //
  // REFERENCE EQUALITY
  //
  //

  static <I, O, A1> DNode<I> Same(String msg, N1<I, O> init, N2<O, A1, I> fn, A1 arg) {
    return v -> dynamicTest(msg, () -> A.Same(v, fn.apply(init.apply(v), arg)));
  }

  //
  //
  // Does not throws
  //
  //

  static <I, O, A1, R> DNode<I> DNT(String msg, N1<I, O> init, N2<O, A1, R> fn, A1 arg) {
    return v -> dynamicTest(msg, () -> A.DNT(() -> fn.apply(init.apply(v), arg)));
  }

  static <I, A1, R> DNode<I> DNT(String msg, N2<I, A1, R> fn, A1 arg) {
    return v -> dynamicTest(msg, () -> fn.apply(v, arg));
  }

  /**
   * Returns a mapping function from {@code I} to {@code DNode<I>} wrapping an
   * {@code A.DNT} call on the execution of the given method reference.
   *
   * @param msg  Test Name
   * @param fn   Function that will be called during the test.
   * @param arg1 First method argument.
   * @param arg2 Second method argument.
   * @param <I>  Stream Value, type of the parent class of the given method
   *             reference.  When the returned test function is executed, the
   *             stream value will be passed to the given method reference as
   *             the instance on which the method should be called.
   * @param <A1> Type of the first defined argument of the method reference.
   * @param <A2> Type of the second defined argument of the method refrence.
   * @param <R>  Return type of the method reference.
   *
   * @return A dynamic test instance for use in JUnit test factories.
   */
  static <I, A1, A2, R> DNode<I> DNT(String msg, FN3<I, A1, A2, R> fn, A1 arg1, A2 arg2) {
    return v -> dynamicTest(msg, () -> fn.apply(v, arg1, arg2));
  }

  //
  //
  // Utils
  //
  //

  @SafeVarargs
  static <T> N1<T, Stream<DynamicNode>> Group(DNode<T>... tests) {
    return val -> Arrays.stream(tests).map(fn -> fn.apply(val));
  }

  interface DNode<I> extends Function<I, DynamicNode> {}
}
