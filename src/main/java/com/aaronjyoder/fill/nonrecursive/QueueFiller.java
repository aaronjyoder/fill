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
    boolean[][] visited = new boolean[image.getHeight()][image.getWidth()];
    Queue<Point> queue = new LinkedList<>();
    queue.add(new Point(x, y));

    while (!queue.isEmpty()) {
      Point p = queue.remove();
      if (FillUtil.isBounded(p.x, p.y, image.getWidth(), image.getHeight()) && image.getRGB(p.x, p.y) == originalRGB && !visited[p.y][p.x]) {
        visited[p.y][p.x] = true;
        image.setRGB(p.x, p.y, fill.getRGB());
        queue.add(new Point(p.x - 1, p.y));
        queue.add(new Point(p.x + 1, p.y));
        queue.add(new Point(p.x, p.y - 1));
        queue.add(new Point(p.x, p.y + 1));
      }
    }
  }

}
