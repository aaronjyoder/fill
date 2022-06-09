package com.aaronjyoder.fill;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

public interface MaskFiller {

  void fill(int x, int y, Color fill, Color maskColor, BufferedImage maskImage);

  default void fill(Point point, Color fill, Color maskColor, BufferedImage maskImage) {
    fill(point.x, point.y, fill, maskColor, maskImage);
  }

}
