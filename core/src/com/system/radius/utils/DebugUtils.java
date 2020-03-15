package com.system.radius.utils;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

public class DebugUtils {

  private DebugUtils() {}

  /**
   * Draw the circle shape as provided.
   *
   * @param s - The shape renderer instance, primed for drawing.
   * @param circle - The circle object to be drawn.
   */
  public static void drawCircle(ShapeRenderer s, Circle circle) {
    s.circle(circle.x, circle.y, circle.radius);
  }

  /**
   * Draw the rectangle shape as provided.
   *
   * @param s - The shape renderer instance, primed for drawing.
   * @param rect - The rectangle object to be drawn.
   */
  public static void drawRect(ShapeRenderer s, Rectangle rect) {
    s.rect(rect.x, rect.y, rect.width, rect.height);
  }

}
