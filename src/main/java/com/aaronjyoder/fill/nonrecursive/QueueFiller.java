package com.aaronjyoder.fill.nonrecursive;

import com.aaronjyoder.fill.Filler;
import com.aaronjyoder.fill.util.FillUtil;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

public class QueueFiller implements Filler {

  private BufferedImage image;

  public QueueFiller(BufferedImage image) {
    this.image = image;
  }

  @Override
  public void fill(int x, int y, Color fill) {
    if (!FillUtil.isBounded(x, y, image.getWidth(), image.getHeight())) {
      return;
    }
    int originalRGB = image.getRGB(x, y);
    if (originalRGB == fill.getRGB()) {
      return;
    }

    Queue<Point> queue = new LinkedList<>();
    if (image.getRGB(x, y) == originalRGB) {
      queue.add(new Point(x, y));

      while (!queue.isEmpty()) {
        Point p = queue.remove();
        if (image.getRGB(p.x, p.y) == originalRGB) {
          int wx = p.x;
          int ex = p.x + 1;

          while (wx >= 0 && image.getRGB(wx, p.y) == originalRGB) {
            wx--;
          }

          while (ex <= image.getWidth() - 1 && image.getRGB(ex, p.y) == originalRGB) {
            ex++;
          }

          for (int ix = wx + 1; ix < ex; ix++) {
            image.setRGB(ix, p.y, fill.getRGB());

            if (p.y - 1 >= 0 && image.getRGB(ix, p.y - 1) == originalRGB) {
              queue.add(new Point(ix, p.y - 1));
            }

            if (p.y + 1 < image.getHeight() && image.getRGB(ix, p.y + 1) == originalRGB) {
              queue.add(new Point(ix, p.y + 1));
            }
          }

        }
      }

    }

  }

}
