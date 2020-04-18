package com.system.radius.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * The super class of all the objects used for the game.
 * All objects are supposed to have their respective position in the game field, whether they are
 * moving or just statically placed.
 */
public abstract class GameObject {

  /**
   * The X-coordinate for this object.
   */
  protected float x;

  /**
   * The Y-coordinate for this object.
   */
  protected float y;

  /**
   * The rate of movement along the X-axis.
   */
  protected float velX;

  /**
   * The rate of movement along the Y-axis.
   */
  protected float velY;

  public GameObject(float x, float y) {

    this.x = x;
    this.y = y;

    this.velX = this.velY = 0;

  }

  /**
   * The method for updating the state of this object.
   *
   * @param delta - The amount of change that has passed.
   */
  public abstract void update(float delta);

  /**
   * The method for drawing this object.
   *  @param batch - The batch object used for drawing.
   *
   */
  public abstract void draw(Batch batch);

  public abstract void drawDebug(ShapeRenderer shapeRenderer);

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public float getVelX() {
    return velX;
  }

  public void setVelX(float velX) {
    this.velX = velX;
  }

  public float getVelY() {
    return velY;
  }

  public void setVelY(float velY) {
    this.velY = velY;
  }

  public void setPosition(float x, float y) {
    setX(x);
    setY(y);
  }

}
