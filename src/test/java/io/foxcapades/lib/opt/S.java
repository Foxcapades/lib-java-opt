package io.foxcapades.lib.opt;

public class S {
  public static class Counter {
    private int count;

    public void inc() {
      count++;
    }

    public <T> T inc(T out) {
      count++;
      return out;
    }

    public int get() {
      return count;
    }
  }
}
