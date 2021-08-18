package io.foxcapades.lib.opt;

import org.junit.jupiter.api.function.Executable;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class A {
  public static <T> void EQ(T exp, T act) {assertEquals(exp, act);}

  public static <E extends Throwable> E Throws(Class<E> e, Executable fn) {
    return assertThrows(e, fn);
  }

  public static void ThrowsNPE(Executable fn) {
    assertThrows(NullPointerException.class, fn);
  }

  public static <T> void Same(T exp, T act)      {assertSame(exp, act);}

  public static void DNT(Executable fn)          {assertDoesNotThrow(fn);}

  public static void True(boolean val)           {assertTrue(val);}
}
