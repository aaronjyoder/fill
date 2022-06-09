package io.github.aaronjyoder.fill.util;

public class FillUtil {

  private FillUtil() {
  }

  public static boolean isBounded(int x, int y, int width, int height) {
    return x >= 0 && y >= 0 && x < width && y < height;
  }

}
