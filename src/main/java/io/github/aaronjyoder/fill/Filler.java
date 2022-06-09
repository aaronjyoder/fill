package io.github.aaronjyoder.fill;

import java.awt.Color;
import java.awt.Point;

public interface Filler {

  void fill(int x, int y, Color fill);

  default void fill(Point point, Color fill) {
    fill(point.x, point.y, fill);
  }

}
