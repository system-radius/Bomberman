package com.system.radius.objects;

/**
 * The abstract game object class for the objeccts related to the Bomberman game.
 */
public abstract class AbstractBomberObject extends GameObject {

  /**
   * A non-null character representation for this object, useful for tracking things in the board
   * state.
   */
  protected char characterRepresentation;

  /**
   * A timer useful for finding the current key frame from an animation object.
   */
  protected float animationElapsedTime;

  /**
   * The life of this object. Details how many times this object can be hit by fire before being
   * destroyed.
   */
  protected int life;

  public AbstractBomberObject(char charRep, float x, float y) {
    super(x, y);
    this.characterRepresentation = charRep;
  }

  /**
   * Burn this object. The definition for the damage this object will receive.
   */
  public abstract void burn();

  public char getCharacterRepresentation() {
    return characterRepresentation;
  }
}
