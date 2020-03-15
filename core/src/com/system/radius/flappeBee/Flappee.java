package com.system.radius.flappeBee;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.system.radius.objects.GameObject;

public class Flappee extends GameObject {

  private static final float COLLISION_RADIUS = 24f;

  private static final float DIVE_ACCEL = 0.30f;

  private static final float FLY_ACCEL = 5f;

  private final Circle collisionCircle;

  public Flappee() {
    super(0, 0);

    this.setVelY(0);
    collisionCircle = new Circle(x, y, COLLISION_RADIUS);
  }

  @Override
  public void update(float delta) {
    velY -= DIVE_ACCEL;

    setPosition(x, y + velY);
  }

  @Override
  public void draw(Batch batch, float delta) {

  }

  @Override
  public void drawDebug(ShapeRenderer shapeRenderer, float delta) {

  }

  public void fly() {
    setVelY(FLY_ACCEL);
    setPosition(x, y + velY);
  }

  public void drawDebug(ShapeRenderer shapeRenderer) {
    shapeRenderer.circle(collisionCircle.x, collisionCircle.y, collisionCircle.radius);
  }

  @Override
  public void setPosition(float x, float y) {
    super.setPosition(x, y);
    collisionCircle.setX(x);
    collisionCircle.setY(y);
  }

}
