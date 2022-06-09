package io.github.aaronjyoder.fill.util;

import java.awt.Color;

public class ColorUtil {

  public static int brightness(Color c) {
    return (int) Math.sqrt(c.getRed() * c.getRed() * 0.241 + c.getGreen() * c.getGreen() * 0.691 + c.getBlue() * c.getBlue() * 0.068);
  }

  public static boolean isDark(Color c) {
    return brightness(c) <= 130;
  }

  public static Color adjustBrightness(Color c, float factor) {
    int a = c.getAlpha();
    int r = Math.round((float) c.getRed() * factor);
    int g = Math.round((float) c.getGreen() * factor);
    int b = Math.round((float) c.getBlue() * factor);
    return new Color(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255), a);
  }

  public static Color adjustBrightnessDynamic(Color c, float whenLight, float whenDark) {
    if (isDark(c)) {
      return adjustBrightness(c, whenDark);
    } else {
      return adjustBrightness(c, whenLight);
    }
  }

}
