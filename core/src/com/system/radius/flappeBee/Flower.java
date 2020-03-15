package com.system.radius.flappeBee;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.system.radius.objects.GameObject;
import com.system.radius.utils.DebugUtils;

public class Flower extends GameObject {

  private static final float PAIR_DISTANCE = 225f;

  private static final float MAX_MS = 100f;

  private static final float COLLISION_RECTANGLE_WIDTH = 13f;

  private static final float COLLISION_RECTANGLE_HEIGHT = 447f;

  private static final float COLLISION_RADIUS = 33f;

  private static final float HEIGHT_OFFSET = -400f;

  public static final float WIDTH = COLLISION_RADIUS * 2;

  private final Circle ceilCircle;

  private final Rectangle ceilRectangle;

  private final Circle floorCircle;

  private final Rectangle floorCollision;

  public Flower() {
    super(0, MathUtils.random(HEIGHT_OFFSET));

    this.floorCollision = new Rectangle(x, y, COLLISION_RECTANGLE_WIDTH,
        COLLISION_RECTANGLE_HEIGHT);
    this.floorCircle = new Circle(x + floorCollision.width / 2, y + floorCollision.height,
        COLLISION_RADIUS);

    this.ceilRectangle = new Rectangle(x, y + COLLISION_RECTANGLE_HEIGHT + PAIR_DISTANCE, COLLISION_RECTANGLE_WIDTH,
        COLLISION_RECTANGLE_HEIGHT);
    this.ceilCircle = new Circle(x + floorCollision.width / 2, ceilRectangle.y, COLLISION_RADIUS);

    System.out.println(y);
  }

  @Override
  public void update(float delta) {

    setPosition(x - (MAX_MS * delta), y);

  }

  @Override
  public void draw(Batch batch, float delta) {

  }

  @Override
  public void drawDebug(ShapeRenderer shapeRenderer, float delta) {

  }

  @Override
  public void setPosition(float x, float y) {
    super.setPosition(x, y);

    floorCollision.setPosition(x, y);
    ceilRectangle.setPosition(x, y + COLLISION_RECTANGLE_HEIGHT + PAIR_DISTANCE);

    floorCircle.setPosition(x + floorCollision.width / 2, y + floorCollision.height);
    ceilCircle.setPosition(x + ceilRectangle.width / 2, ceilRectangle.y);
  }

  public void drawDebug(ShapeRenderer shapeRenderer) {
    DebugUtils.drawCircle(shapeRenderer, floorCircle);
    DebugUtils.drawRect(shapeRenderer, floorCollision);

    DebugUtils.drawCircle(shapeRenderer, ceilCircle);
    DebugUtils.drawRect(shapeRenderer, ceilRectangle);
  }

}
