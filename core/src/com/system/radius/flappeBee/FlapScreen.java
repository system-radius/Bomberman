package com.system.radius.flappeBee;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.system.radius.screens.AbstractScreen;

public class FlapScreen extends AbstractScreen {

  private static final float FLOWER_GAP = 200f;

  private ShapeRenderer shapeRenderer;

  private Viewport viewport;

  private Camera camera;

  private Flappee flappee;

  private Array<Flower> flowers = new Array<>();

  public FlapScreen(Game game) {
    super(game, 48, 64, 10);
    flappee = new Flappee();
  }

  @Override
  public void show() {

    camera = new OrthographicCamera();
    camera.position.set(scaledWorldWidth / 2, scaledWorldHeight / 2, 0);
    camera.update();

    viewport = new FitViewport(scaledWorldWidth, scaledWorldHeight, camera);

    shapeRenderer = new ShapeRenderer();

    flappee.setPosition(scaledWorldWidth / 4, scaledWorldHeight / 2);

  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height);
  }

  public void update(float delta) {
    flappee.update(delta);
    for (Flower flower : flowers) {
      flower.update(delta);
    }
    checkFlowers();

    if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
      flappee.fly();
    }

    clampFlappee();
  }

  public void draw(float delta) {

    spriteBatch.setProjectionMatrix(camera.projection);
    spriteBatch.setTransformMatrix(camera.view);

    spriteBatch.begin();

    shapeRenderer.setProjectionMatrix(camera.projection);
    shapeRenderer.setTransformMatrix(camera.view);

    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    flappee.drawDebug(shapeRenderer);

    for (Flower flower : flowers) {
      flower.drawDebug(shapeRenderer);
    }

    shapeRenderer.end();

    spriteBatch.end();
  }

  private void clampFlappee() {
    flappee.setPosition(flappee.getX(), MathUtils.clamp(flappee.getY(), 0, scaledWorldHeight));
  }

  private void checkFlowers () {
    if (flowers.size == 0) {
      createNewFlower();
    } else {
      Flower flower = flowers.peek();
      if (flower.getX() < scaledWorldWidth - FLOWER_GAP) {
        createNewFlower();
      }
    }

    if (flowers.size > 0) {
      Flower flower = flowers.first();
      if (flower.getX() < -Flower.WIDTH) {
        flowers.removeValue(flower, true);
      }
    }

  }

  private void createNewFlower() {
    Flower flower = new Flower();
    flower.setPosition(scaledWorldWidth + Flower.WIDTH, flower.getY());

    flowers.add(flower);
  }

}
