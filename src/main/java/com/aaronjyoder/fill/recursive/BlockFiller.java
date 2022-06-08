package com.aaronjyoder.fill.recursive;

import com.aaronjyoder.fill.Filler;
import com.aaronjyoder.fill.util.FillUtil;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Original algorithm concept by Adam Milazzo.
 * All code in this file written from scratch by Aaron Yoder.
 *
 * @see <a href="http://www.adammil.net/blog/v126_A_More_Efficient_Flood_Fill.html">A More Efficient Flood Fill</a>
 */
public class BlockFiller implements Filler {

  private BufferedImage image;

  public BlockFiller(BufferedImage image) {
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

    // Check if we can paint the starting position before continuing
    if (canPaint(x, y, originalRGB)) {
      fillStart(x, y, originalRGB, fill.getRGB(), image.getWidth(), image.getHeight());
    }
  }

  /**
   * This method moves the "cursor" to the top-left-most position that it can and then proceeds to fill from there.
   *
   * @param x      The x coordinate to start at.
   * @param y      The y coordinate to start at.
   * @param width  The width of the image.
   * @param height The height of the image.
   */
  private void fillStart(int x, int y, int originalRGB, int fillRGB, int width, int height) {
    // Move to the upper left-most spot before filling. The loop stops if it reaches a location where it can no longer move, such as (0, 0).
    while (true) {
      int xPrev = x, yPrev = y;
      while (y != 0 && canPaint(x, y - 1, originalRGB)) {
        y--;
      }
      while (x != 0 && canPaint(x - 1, y, originalRGB)) {
        x--;
      }
      if (x == xPrev && y == yPrev) {
        break;
      }
    }
    // After that, go to the core algorithm.
    fillCore(x, y, originalRGB, fillRGB, width, height);
  }

  /**
   * The core filling algorithm. Does some initial checks, then paints a row to the right, then attempts to repeat going in a rectangular fashion.
   *
   * @param x      The x coordinate to start filling at.
   * @param y      The y coordinate to start filling at.
   * @param width  The width of the image.
   * @param height The height of the image.
   */
  private void fillCore(int x, int y, int originalRGB, int fillRGB, int width, int height) {
    // Keep track of the length of the previous row we filled so that we can determine how to fill the row after it
    int prevRowLength = 0;
    // Attempt to fill a rectangular area, scanning down and to the right.
    do {
      // Used to keep track of how long the current row is.
      // xStart is the starting point for the scan, which we need since we alter the value of x throughout.
      int rowLength = 0, xStart = x;
      // If we painted the previous row, but the pixel we're on now can't be painted, move x to the right until it can be painted.
      // This it to handle the case where there's an "overhang" without recursion.
      if (prevRowLength != 0 && !canPaint(x, y, originalRGB)) {
        do {
          if (--prevRowLength == 0) { // If prevRowLength reaches 0, that means we've reached the end of the rectangle we are trying to paint, so we return out.
            return;
          }
        } while (!canPaint(++x, y, originalRGB)); // Move x to the right and check if we can paint there. If we can, we can continue to painting.
        xStart = x; // set xStart to x since this is the new starting position we'll be painting from
      } else { // This "else" handles the opposite case of the "overhang", where there is now a position to the left that can be painted. Again, this avoids recursion.
        // We want to move to the left as much as possible and paint as we go, so that we can give the rest of the algorithm the best chance to fill in a very large rectangle.
        for (; x != 0 && canPaint(x - 1, y, originalRGB); rowLength++, prevRowLength++) {
          // We can paint to the left of where we are now, so decrement x and then do it to avoid checking these cells again later.
          // At the same time, update rowLength to reflect the cells being filled.
          image.setRGB(--x, y, fillRGB);
          // Start a new fill in the spot above where we are (if we can) because there may be more to paint above where we now are.
          if (y != 0 && canPaint(x, y - 1, originalRGB)) {
            fillStart(x, y - 1, originalRGB, fillRGB, width, height); // Use fillStart, so we go up and to the left as much as possible before filling.
          }
        }
      }

      // We are now at a point where we can scan the current row of a rectangular area.
      // It is assumed that [x, x + prevRowLength) has already been filled, so don't need to check it.
      // So, start scanning to the right and filling.
      for (; xStart < width && canPaint(xStart, y, originalRGB); rowLength++, xStart++) {
        image.setRGB(xStart, y, fillRGB);
      }
      // We filled as much as we possibly could at this point.
      // If the area we're filling is rectangular, we don't need to check above since we already filled it, and we don't need to check below since we will fill it next iteration.
      // However, if the area is not rectangular, we may need to check up or to the right.

      // If this row is shorter than the previous row, we may need to look to the right of the end of this row because there could be a blockage, and then more to fill.
      if (rowLength < prevRowLength) {
        // prevRowEnd is the end of the previous row, which is just x + prevRowLength.
        // Since we filled up to xStart already, we want to keep scanning from xStart+1 to the end of the previous row to get through any blockage.
        for (int prevRowEnd = x + prevRowLength; ++xStart < prevRowEnd; ) {
          if (canPaint(xStart, y, originalRGB)) {
            // We know in this situation that we painted up and to the left already, so we don't need to worry about it, thus, use fillCore.
            fillCore(xStart, y, originalRGB, fillRGB, width, height);
          }
        }
      } else if (rowLength > prevRowLength && y
          != 0) { // If this row is longer than the previous row, we may have to look above since there may have been a blockage from above that prevented the previous row from getting as long.
        // xStart is the end of the current row at this point.
        // We want to check above the end of the previous row. So we start at x + prevRowLength.
        // Say the previous row was length 3, and this row is length 5. We want to check x=4 and x=5 in the row above.
        // So, we start at x + prevRowLength, and check until the end of the current row.
        for (int xUpper = x + prevRowLength; ++xUpper < xStart; ) {
          if (canPaint(xUpper, y - 1, originalRGB)) {
            fillStart(xUpper, y - 1, originalRGB, fillRGB, width, height); // Use fillStart, so we go up and to the left as much as possible before filling.
          }
        }
      }
      prevRowLength = rowLength;
    } while (prevRowLength != 0 && ++y < height); // If the previous row length was 0, or we reach the bottom, then stop.
  }

  private boolean canPaint(int x, int y, int originalRGB) {
    return image.getRGB(x, y) == originalRGB;
  }

}
