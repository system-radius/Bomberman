package com.system.radius.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * An abstract screen with world size representation. The camera and viewport instances are to be
 * created in the inheriting classes.
 */
public abstract class AbstractScreen extends ScreenAdapter {

  protected Game game;

  protected SpriteBatch spriteBatch = new SpriteBatch();

  protected Color color = Color.BLACK;

  protected int screenWidth = Gdx.graphics.getWidth();

  protected int screenHeight = Gdx.graphics.getHeight();

  protected float worldWidth;

  protected float scaledWorldWidth;

  protected float worldHeight;

  protected float scaledWorldHeight;

  protected float scale;

  public AbstractScreen(Game game, float worldWidth, float worldHeight, float scale) {

    this.game = game;

    this.worldWidth = worldWidth;
    this.worldHeight = worldHeight;

    this.scaledWorldWidth = worldWidth * scale;
    this.scaledWorldHeight = worldHeight * scale;

    this.scale = scale;
  }

  /**
   * Clears the screen with the specified color.
   */
  public void clearScreen() {

    Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

  }

  /**
   * Update the game objects.
   *
   * @param delta - The amount of change that has passed.
   */
  public abstract void update(float delta);

  /**
   * Draw the game objects.
   *
   * @param delta - The amount of change that has passed.
   */
  public abstract void draw(float delta);

  @Override
  public final void render(float delta) {
    update(delta);

    clearScreen();
    draw(delta);
  }

}
