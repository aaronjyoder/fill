package io.github.aaronjyoder.fill;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

public interface MaskFiller {

  void fill(int x, int y, Color fill, BufferedImage maskImage);

  default void fill(Point point, Color fill, BufferedImage maskImage) {
    fill(point.x, point.y, fill, maskImage);
  }

}
